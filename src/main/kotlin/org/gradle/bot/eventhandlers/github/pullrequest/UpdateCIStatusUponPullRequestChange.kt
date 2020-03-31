package org.gradle.bot.eventhandlers.github.pullrequest

import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.eventhandlers.github.AbstractGitHubEventHandler
import org.gradle.bot.model.CommitStatusState
import org.gradle.bot.model.PullRequestGitHubEvent
import org.jetbrains.teamcity.rest.Build
import org.slf4j.LoggerFactory

val ciStatusContext = "CI Status"

fun ciStatusDesc(build: Build, commitStatusState: CommitStatusState) =
    "${build.branch.name} branch ${commitStatusState.name.toLowerCase()} since ${build.finishDateTime!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}"

// https://developer.github.com/v3/activity/events/types/#pullrequestevent

enum class PullRequestAction {
    ASSIGNED, UNASSIGNED, LABELED, UNLABELED, OPENED, EDITED, CLOSED, REOPENED, SYNCHRONIZE, READY_FOR_REVIEW, LOCKED, UNLOCKED;

    companion object {
        fun of(value: String) = valueOf(value.toUpperCase())
    }
}

fun PullRequestGitHubEvent.getWebUrl() = "https://github.com/${pullRequest.base.repo.fullName}/${pullRequest.number}"

fun PullRequestGitHubEvent.getRepoFullName() = pullRequest.base.repo.fullName

fun PullRequestGitHubEvent.getHeadSha() = pullRequest.head.sha

fun PullRequestGitHubEvent.getTargetBranch() = pullRequest.base.ref

/**
 * This handler publishes a "CI Status" commit status whenever a member PR is created/synchronized
 * so the developer can see the latest CI status directly from PR page.
 */
@Singleton
class UpdateCIStatusUponPullRequestChange @Inject constructor(
    private val githubClient: GitHubClient,
    private val teamCityClient: TeamCityClient
) :
    AbstractGitHubEventHandler<PullRequestGitHubEvent>() {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun handleEvent(event: PullRequestGitHubEvent) {
        if (PullRequestAction.of(event.action).let { it != PullRequestAction.OPENED && it != PullRequestAction.SYNCHRONIZE }) {
            logger.debug("Skip pull request {} with action {}", event.getWebUrl(), event.action)
            return
        }

        if (event.pullRequest.head.repo.fork) {
            logger.debug("Skip publishing CI status for forked pull request {}", event.getWebUrl())
        }

        teamCityClient.getLatestFinishedBuild(event.getTargetBranch()).onSuccess {
            val commitStatusState = CommitStatusState.fromTeamCityBuildStatus(it.status!!)
            githubClient.createCommitStatus(event.getRepoFullName(),
                event.getHeadSha(),
                commitStatusState,
                it.getHomeUrl(),
                ciStatusDesc(it, commitStatusState),
                ciStatusContext)
        }
    }
}
