package org.gradle.bot.eventhandlers.github.commitstatus

import javax.inject.Inject
import javax.inject.Singleton
import org.gradle.bot.eventhandlers.github.AbstractGitHubEventHandler
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestManager
import org.gradle.bot.model.CommitStatusGitHubEvent
import org.gradle.bot.model.CommitStatusObject
import org.gradle.bot.model.CommitStatusState

fun CommitStatusGitHubEvent.state() = CommitStatusState.of(state)
fun CommitStatusGitHubEvent.commit() = commit.sha

@Singleton
class CommitStatusEventHandler @Inject constructor(
    private val pullRequestManager: PullRequestManager
) : AbstractGitHubEventHandler<CommitStatusGitHubEvent>() {
    override fun handleEvent(event: CommitStatusGitHubEvent) {
        pullRequestManager.updateCommitStatus(
            event.repository.fullName,
            event.commit.sha,
            CommitStatusObject(event.state, event.targetUrl, event.description, event.context)
        )
    }
}
