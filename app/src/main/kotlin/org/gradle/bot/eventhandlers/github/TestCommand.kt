package org.gradle.bot.eventhandlers.github

import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import org.gradle.bot.client.getAllDependencies
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequest
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestCommand
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestComment
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestContext
import org.gradle.bot.model.BuildStage
import org.gradle.bot.model.CommitStatusObject
import org.gradle.bot.model.CommitStatusState
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildState

class TestCommand(val targetStage: BuildStage, private val sourceComment: PullRequestComment) : PullRequestCommand {
    val pullRequest: PullRequest
        get() = sourceComment.pullRequest

    override fun execute(context: PullRequestContext) {
        findLatestTriggeredBuild(context).onSuccess {
            if (it == null) {
                triggerBuild(context)
            } else if (it.state == BuildState.QUEUED || it.state == BuildState.RUNNING) {
                cancelPreviousRunningBuildAndTriggerNewBuild(context, it)
            } else {
                triggerBuild(context)
            }
        }.onFailure {
            context.gitHubClient.reply(sourceComment, contactAdminComment)
        }
    }

    private fun triggerBuild(context: PullRequestContext, messageBuilder: (Build) -> String) {
        context.teamCityClient.triggerBuild(targetStage, pullRequest.teamCityBranchName).onSuccess {build ->
            updatePendingStatuses(context, build, targetStage)
            context.reply(sourceComment,
                messageBuilder(build),
                build.id.stringId)
        }.onFailure {
            context.gitHubClient.reply(sourceComment, contactAdminComment)
        }
    }

    private fun cancelPreviouslyTriggeredBuild(context: PullRequestContext, build: Build): CompositeFuture =
        CompositeFuture.all(build.getAllDependencies().map {
            context.cancelBuild(build, "The user triggered a new build.")
        })

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
    private fun updatePendingStatuses(context: PullRequestContext, build: Build, targetBuildStage: BuildStage) {
        val buildConfigurationIdToBuildMap = build.getAllDependencies().map { it.buildConfigurationId.stringId to it }.toMap()
        val currentSuccessStatuses: List<String> = pullRequest.commitStatuses
            .filter { it.state == CommitStatusState.SUCCESS.toString() }.map { it.context }

        val dependencies = targetBuildStage.dependencies.toMutableList()
        dependencies.removeIf { currentSuccessStatuses.contains(it.configName) }

        val commitStatuses: List<CommitStatusObject> = dependencies.map {
            CommitStatusObject(
                CommitStatusState.PENDING.name.toLowerCase(),
                buildConfigurationIdToBuildMap[it.id]?.getHomeUrl(),
                "TeamCity build running",
                it.configName
            )
        }

        context.createCommitStatus(pullRequest.repoName, pullRequest.headCommitSha, commitStatuses)
    }

    private fun findLatestTriggeredBuild(context: PullRequestContext): Future<Build?> {
        val commentWithBuildId = sourceComment.pullRequest.comments.findLast { it.metadata.teamCityBuildId != null }
        return if (commentWithBuildId == null) {
            Future.succeededFuture(null)
        } else {
            context.findBuild(commentWithBuildId.metadata.teamCityBuildId!!)
        }
    }

    private fun cancelPreviousRunningBuildAndTriggerNewBuild(context: PullRequestContext, oldBuild: Build) {
        cancelPreviouslyTriggeredBuild(context, oldBuild).onSuccess {
            triggerBuild(context) { newBuild ->
                "OK, I've already cancelled the [old build](${oldBuild.getHomeUrl()}) and triggered a new [${targetStage.fullName} build](${newBuild.getHomeUrl()}) for you."
            }
        }.onFailure {
            context.reply(sourceComment, contactAdminComment)
        }
    }

    private fun triggerBuild(context: PullRequestContext) {
        triggerBuild(context) {
            "OK, I've already triggered [${targetStage.fullName} build](${it.getHomeUrl()}) for you."
        }
    }
}