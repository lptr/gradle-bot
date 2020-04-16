package org.gradle.bot.eventhandlers.github

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequest
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestCommand
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestComment
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestContext
import org.gradle.bot.model.BuildConfiguration
import org.gradle.bot.model.BuildStage
import org.gradle.bot.model.CommitStatusObject
import org.gradle.bot.model.CommitStatusState
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildState
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TestCommand(val targetStage: BuildStage, private val sourceComment: PullRequestComment) : PullRequestCommand {
    val logger: Logger = LoggerFactory.getLogger(javaClass)
    val pullRequest: PullRequest
        get() = sourceComment.pullRequest

    override fun execute(context: PullRequestContext) {
        findLatestTriggeredBuild(context).compose {
            triggerBuild(context, it) as Future<Any>
        }.onFailure {
            logger.error("", it)
            context.gitHubClient.reply(sourceComment, contactAdminComment)
        }
    }

    private fun triggerBuild(context: PullRequestContext, build: Build?): Future<*> =
        if (build == null) {
            doTriggerBuild(context)
        } else if (build.state == BuildState.QUEUED || build.state == BuildState.RUNNING) {
            cancelPreviousRunningBuildAndTriggerNewBuild(context, build)
        } else {
            doTriggerBuild(context)
        }

    private fun doTriggerBuild(context: PullRequestContext, messageBuilder: (Build) -> String) =
        context.teamCityClient.triggerBuild(targetStage, pullRequest.teamCityBranchName).onSuccess { build ->
            updatePendingStatuses(context, build, targetStage)
            context.reply(sourceComment,
                messageBuilder(build),
                build.id.stringId)
        }

    /**
     * Only update pending statuses for failure statuses.
     *
     * For example, user triggers a build, all statuses will be updated to pending.
     *
     * Then early steps of the builds succeeds, late steps fails.
     *
     * User thinks it's not his fault, so he triggers a new build again.
     *
     * Now the early successful steps will not be rerun by TeamCity, so we should not update them.
     */
    private fun updatePendingStatuses(context: PullRequestContext, build: Build, targetBuildStage: BuildStage): Future<*> =
        context.getAllDependencies(build).compose { buildDependencies ->
            val buildConfigurationIdToBuildMap = buildDependencies.map { it.buildConfigurationId.stringId to it }.toMap()

            val currentSuccessStatuses: List<String> = pullRequest.commitStatuses
                .filter { it.state == CommitStatusState.SUCCESS.toString() }.map { it.context }

            val dependenciesToBeUpdated = targetBuildStage.getAllBuildConfigurationDependencies().toMutableList()
            dependenciesToBeUpdated.removeIf { currentSuccessStatuses.contains(it.configName) }

            val commitStatuses: List<CommitStatusObject> = dependenciesToBeUpdated.map {
                CommitStatusObject(
                    CommitStatusState.PENDING.name.toLowerCase(),
                    buildConfigurationIdToBuildMap[it.id]?.getHomeUrl(),
                    "TeamCity build running",
                    it.configName
                )
            }

            logger.debug("Builds: {}, current statuses: {}, updated statuses: {}",
                buildDependencies.filter { BuildConfiguration.containsBuild(it) },
                pullRequest.commitStatuses,
                commitStatuses
            )

            context.createCommitStatus(pullRequest.repoName, pullRequest.headCommitSha, commitStatuses)
        }.onFailure {
            logger.error("", it)
        }

    private fun findLatestTriggeredBuild(context: PullRequestContext): Future<Build?> {
        val commentWithBuildId = sourceComment.pullRequest.comments.findLast { it.metadata.teamCityBuildId != null }
        return if (commentWithBuildId == null) {
            Future.succeededFuture(null)
        } else {
            context.findBuild(commentWithBuildId.metadata.teamCityBuildId!!)
        }
    }

    private fun cancelPreviousRunningBuildAndTriggerNewBuild(context: PullRequestContext, oldBuild: Build) =
        context.getAllDependencies(oldBuild).compose { builds ->
            CompositeFuture.join(builds.map { context.cancelBuild(it, "The user triggered a new build.") })
        }.onComplete {
            doTriggerBuild(context) { newBuild ->
                "OK, I've already cancelled the [old build](${oldBuild.getHomeUrl()}) and triggered a new [${targetStage.fullName} build](${newBuild.getHomeUrl()}) for you."
            }
        }

    private fun doTriggerBuild(context: PullRequestContext) =
        doTriggerBuild(context) {
            "OK, I've already triggered [${targetStage.fullName} build](${it.getHomeUrl()}) for you."
        }
}