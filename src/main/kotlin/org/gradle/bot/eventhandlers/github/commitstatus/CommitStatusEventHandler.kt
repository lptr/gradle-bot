package org.gradle.bot.eventhandlers.github.commitstatus

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.eventhandlers.github.AbstractGitHubEventHandler
import org.gradle.bot.eventhandlers.github.pullrequest.CommitToPullRequestInfoLocalCacheHandler
import org.gradle.bot.model.CommitStatusGitHubEvent
import org.gradle.bot.model.CommitStatusState
import org.slf4j.LoggerFactory

fun CommitStatusGitHubEvent.state() = CommitStatusState.valueOf(state)
fun CommitStatusGitHubEvent.commit() = commit.sha

@Singleton
class CommitStatusEventHandler @Inject constructor(
    private val gitHubClient: GitHubClient,
    private val commitToPullRequestInfoLocalCacheHandler: CommitToPullRequestInfoLocalCacheHandler
) : AbstractGitHubEventHandler<CommitStatusGitHubEvent>() {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun handleEvent(event: CommitStatusGitHubEvent) {
        if (event.state() != CommitStatusState.SUCCESS) {
            logger.debug("Skip commit status event {} {}", event.state(), event.commit())
            return
        }

        if (processedCommits.containsKey(event.commit())) {
            logger.debug("Skip commit {} which is already processed", event.commit())
            return
        }
    }

    companion object {
        val processedCommits: ConcurrentHashMap<String, CommitStatusInformation> = ConcurrentHashMap()
    }
}

class CommitStatusInformation
