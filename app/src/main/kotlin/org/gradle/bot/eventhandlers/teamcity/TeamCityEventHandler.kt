package org.gradle.bot.eventhandlers.teamcity

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.Future
import io.vertx.core.eventbus.Message
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.eventhandlers.WebHookEventHandler
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestManager
import org.gradle.bot.eventhandlers.github.pullrequest.ciStatusContext
import org.gradle.bot.eventhandlers.github.pullrequest.ciStatusDesc
import org.gradle.bot.model.BuildStage
import org.gradle.bot.model.CommitStatusState
import org.gradle.bot.model.CommitStatusState.Companion.of
import org.gradle.bot.model.ListOpenPullRequestsResponse
import org.gradle.bot.objectMapper
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildStatus
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

interface TeamCityEventHandler : WebHookEventHandler {
    override val eventPrefix: String
        get() = "teamcity"
}

enum class BuildEventStatus {
    SUCCESS,
    FAILURE,
    RUNNING
}

/*
{
"text":" success - *Test Coverage - Quick Java8 Oracle Windows (pluginUse_2)* <https://builds.gradle.org/viewLog.html?buildTypeId=Gradle_Check_Quick_2_bucket30&buildId=33115638|#1503> (triggered by Snapshot dependency; Jendrik Johannes; Gradle / Check / Ready for Merge (Trigger))"
}
 */

val buildEventPattern = "(success|failure|running) - .*buildTypeId=(\\w+)&buildId=(\\d+)\\|.*".toRegex()

class TeamCityBuildEvent(@JsonProperty("text") val text: String) {
    val buildStatus: BuildEventStatus
    val buildTypeId: String
    val buildId: String

    init {
        val result: MatchResult = buildEventPattern.find(text)!!
        buildStatus = BuildEventStatus.valueOf(result.groupValues[1].toUpperCase())
        buildTypeId = result.groupValues[2]
        buildId = result.groupValues[3]
    }
}

abstract class AbstractTeamCityEventHandler : TeamCityEventHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    override val eventType: String
        get() = "build"

    override fun handle(event: Message<String>?) {
        try {
            handleEvent(objectMapper.readValue(event?.body(), TeamCityBuildEvent::class.java))
        } catch (e: Throwable) {
            logger.error(event?.body(), e)
        }
    }

    abstract fun handleEvent(event: TeamCityBuildEvent)
}

fun ListOpenPullRequestsResponse.Node.getHeadCommit() = headRef.target.oid

// 2018-11-26T22:23:45Z
fun ListOpenPullRequestsResponse.Node.lastCommittedDate() = ZonedDateTime.parse(commits.nodes[0].commit.committedDate)
fun ListOpenPullRequestsResponse.Node.isStale() = Duration.between(lastCommittedDate().toInstant(), Instant.now()).toDays() > 30
fun ListOpenPullRequestsResponse.Node.getTargetBranch() = baseRefName

/**
 * Upon receiving failure TeamCity build stage webhooks, we first try to find out the target PR.
 * If found, check if any of its dependencies is flaky. If so, rerun the stage build.
 */
@Singleton
class AutoRetryFlakyBuild @Inject constructor(
    private val pullRequestManager: PullRequestManager,
    private val teamCityClient: TeamCityClient
) : AbstractTeamCityEventHandler() {
    private val logger = LoggerFactory.getLogger(AutoRetryFlakyBuild::class.java)
    override fun handleEvent(event: TeamCityBuildEvent) {
        val stage = BuildStage.fromBuildTypeId(event.buildTypeId)
        if (stage == null) {
            logger.debug("Skip non-stage build {}", event.buildId)
            return
        }
        if (event.buildStatus != BuildEventStatus.FAILURE) {
            logger.debug("Skip build {} since it's not found in any pull requests")
        } else {
            val pr = pullRequestManager.findPullRequestByBuildId(event.buildId)
            if (pr == null) {
                logger.debug("No pull request found with build id {}, skip.", event.buildId)
            } else {
                maybeRerunBuild(stage, pr.teamCityBranchName, event)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun maybeRerunBuild(stage: BuildStage, branchName: String, event: TeamCityBuildEvent) =
        teamCityClient.findBuild(event.buildId).compose { it ->
            teamCityClient.searchFlakyBuildInDependencies(it!!)
        }.compose { flakyBuild ->
            if (flakyBuild == null) {
                logger.debug("No flaky tests found, skip.")
                Future.succeededFuture()
            } else {
                rerunBuildIfFirstTime(stage, branchName, flakyBuild) as Future<Any>
            }
        }.onFailure {
            logger.error("", it)
        }

    private fun rerunBuildIfFirstTime(stage: BuildStage, branch: String, flakyBuild: Build): Future<*> =
        teamCityClient.needsToRerunFlakyBuild(flakyBuild).compose {
            // Check if the flaky build needs to be rerun
            if (it) {
                logger.info("Rerunning stage {} because of flaky build {}", stage, flakyBuild.id.stringId)
                teamCityClient.triggerBuild(stage, branch)
            } else {
                logger.info("No need to rerun flaky build {} because this is not the first time.", flakyBuild.id.stringId)
                Future.succeededFuture()
            }
        }
}

/**
 * Upon Ready for Nightly builds finish, update all open pull request's head commit with the build status
 */
@Singleton
class UpdateCIStatusForAllOpenPullRequests @Inject constructor(
    private val gitHubClient: GitHubClient,
    private val teamCityClient: TeamCityClient
) : AbstractTeamCityEventHandler() {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val repoName = "gradle/gradle"
    private val acceptedStatus = listOf(BuildStatus.SUCCESS.toString(), BuildStatus.FAILURE.toString())
    private val acceptedBranches = listOf("master", "release")
    private val acceptedBuildTypeId = BuildStage.READY_FOR_NIGHTLY.buildTypeId
    override fun handleEvent(event: TeamCityBuildEvent) {
        if (!acceptedStatus.contains(event.buildStatus.toString()) ||
            event.buildTypeId != acceptedBuildTypeId) {
            logger.debug("Skip teamcity event {}", event.text)
            return
        }
        teamCityClient.findBuild(event.buildId).onSuccess { build ->
            if (build!!.branch.name !in acceptedBranches) {
                logger.debug("Skip teamcity build event on branch {}", build.branch.name)
                return@onSuccess
            }
            gitHubClient.listOpenPullRequests(repoName).onSuccess {
                it.data.repository.pullRequests.nodes.forEach { pr ->
                    updateCIStatusFor(build, pr)
                }
            }
        }
    }

    private fun updateCIStatusFor(build: Build, pr: ListOpenPullRequestsResponse.Node) {
        if (pr.isStale()) {
            logger.debug("Skip stale PR: {}", pr.url)
            return
        }
        val latestCIStatus: CommitStatusState? = pr.commits?.nodes?.get(0)?.commit?.status?.contexts
            ?.find { it.context == ciStatusContext }?.state?.let(::of)

        val targetStatus = of(build.status.toString())

        if (targetStatus != latestCIStatus && build.branch.name.equals(pr.getTargetBranch())) {
            logger.debug("Update CI status {} to {} {}", targetStatus, pr.url, pr.getHeadCommit())
            gitHubClient.createCommitStatus(
                repoName,
                pr.getHeadCommit(),
                targetStatus,
                build.getHomeUrl(),
                ciStatusDesc(build, targetStatus),
                ciStatusContext
            )
        }
    }
}
