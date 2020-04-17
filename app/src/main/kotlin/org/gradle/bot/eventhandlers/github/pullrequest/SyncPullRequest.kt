package org.gradle.bot.eventhandlers.github.pullrequest

import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.eventhandlers.github.AbstractGitHubEventHandler
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestAction.OPENED
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestAction.REOPENED
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestAction.SYNCHRONIZE
import org.gradle.bot.model.CommitStatusState
import org.gradle.bot.model.PullRequestGitHubEvent
import org.jetbrains.teamcity.rest.Build
import org.slf4j.LoggerFactory

const val ciStatusContext = "CI Status"

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
class SyncPullRequest @Inject constructor(
    private val gitHubClient: GitHubClient,
    private val pullRequestManager: PullRequestManager
) :
    AbstractGitHubEventHandler<PullRequestGitHubEvent>() {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val acceptedActions = listOf(OPENED, SYNCHRONIZE, REOPENED)
    private val acceptedTargetBranches = listOf("master", "release")
    override fun handleEvent(event: PullRequestGitHubEvent) {
        if (PullRequestAction.of(event.action) !in acceptedActions ||
            event.getTargetBranch() !in acceptedTargetBranches) {
            logger.debug("Skip pull request {} with action {}", event.getWebUrl(), event.action)
        } else {
            gitHubClient.getPullRequestWithComments(event.repository.fullName, event.pullRequest.number).onSuccess {
                pullRequestManager.update(PullRequest(gitHubClient.whoAmI(), it))
            }
        }
    }
}
