package org.gradle.bot.eventhandlers.github.commitstatus

import org.gradle.bot.client.GitHubClient
import org.gradle.bot.eventhandlers.github.AbstractGitHubEventHandler
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestManager
import org.gradle.bot.model.CommitStatusGitHubEvent
import org.gradle.bot.model.CommitStatusObject
import org.gradle.bot.model.CommitStatusState
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

fun CommitStatusGitHubEvent.state() = CommitStatusState.of(state)
fun CommitStatusGitHubEvent.commit() = commit.sha

@Singleton
class CommitStatusEventHandler @Inject constructor(
    private val gitHubClient: GitHubClient,
    private val pullRequestManager: PullRequestManager
) : AbstractGitHubEventHandler<CommitStatusGitHubEvent>() {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun handleEvent(event: CommitStatusGitHubEvent) {
        pullRequestManager.updateCommitStatus(
            event.repository.fullName,
            event.commit.sha,
            CommitStatusObject(event.state, event.targetUrl, event.description, event.context)
        )
    }
}

