package org.gradle.bot.eventhandlers.github.issuecomment

import javax.inject.Inject
import javax.inject.Singleton
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.eventhandlers.github.AbstractGitHubEventHandler
import org.gradle.bot.model.IssueCommentGitHubEvent

@Singleton
class IssueCommentEventHandler @Inject constructor(
    private val gitHubClient: GitHubClient,
    private val teamCityClient: TeamCityClient
) : AbstractGitHubEventHandler<IssueCommentGitHubEvent>() {
    override fun handleEvent(event: IssueCommentGitHubEvent) {
        gitHubClient.getPullRequestWithComments(event.repository.fullName, event.issue.number).onSuccess {
            PullRequestContext(gitHubClient, teamCityClient, it).processCommand(event.comment.id)
        }
    }
}
