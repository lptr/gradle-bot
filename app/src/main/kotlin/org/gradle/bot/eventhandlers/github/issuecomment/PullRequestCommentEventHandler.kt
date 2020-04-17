package org.gradle.bot.eventhandlers.github.issuecomment

import javax.inject.Inject
import javax.inject.Singleton
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.eventhandlers.github.AbstractGitHubEventHandler
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequest
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestManager
import org.gradle.bot.model.IssueCommentGitHubEvent
import org.slf4j.LoggerFactory

fun IssueCommentGitHubEvent.isPullRequest() = issue.pullRequest != null

fun IssueCommentGitHubEvent.getCommentWebUrl() = "https://github.com/${repository.fullName}/issues/${issue.number}#issuecomment-${comment.id}"

@Singleton
class PullRequestCommentEventHandler @Inject constructor(
    private val gitHubClient: GitHubClient,
    private val pullRequestManager: PullRequestManager
) : AbstractGitHubEventHandler<IssueCommentGitHubEvent>() {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleEvent(event: IssueCommentGitHubEvent) {
        if (!event.isPullRequest()) {
            logger.info("Skip issue comment {}", event.getCommentWebUrl())
            return
        }
        gitHubClient.getPullRequestWithComments(event.repository.fullName, event.issue.number).onSuccess {
            val pullRequest = PullRequest(gitHubClient.whoAmI(), it)
            pullRequest.addCommentIfNotExist(event.comment.id,
                event.comment.user.login,
                event.comment.authorAssociation,
                event.comment.body,
                gitHubClient.whoAmI()
            )

            if (event.comment.id != pullRequest.comments.last().id) {
                // In case of race condition
                logger.warn("Current comment: {}, the last comment of pull request query response: {}", event.comment.id, pullRequest.comments.last().id)
            }
            pullRequestManager.update(pullRequest)
        }
    }
}
