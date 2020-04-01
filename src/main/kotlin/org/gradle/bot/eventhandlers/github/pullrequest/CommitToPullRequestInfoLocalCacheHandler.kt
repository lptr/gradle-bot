package org.gradle.bot.eventhandlers.github.pullrequest

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import javax.inject.Singleton
import org.gradle.bot.eventhandlers.github.AbstractGitHubEventHandler
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestAction.CLOSED
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestAction.OPENED
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestAction.REOPENED
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestAction.SYNCHRONIZE
import org.gradle.bot.model.PullRequestGitHubEvent
import org.slf4j.LoggerFactory

/**
 * It's impossible to find a PR and corresponding branch from a commit id via API,
 *
 */
@Singleton
class CommitToPullRequestInfoLocalCacheHandler : AbstractGitHubEventHandler<PullRequestGitHubEvent>() {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val commitIdToPullRequestsCache: Multimap<String, PullRequestInformation> =
        Multimaps.synchronizedMultimap(HashMultimap.create())
    private val acceptedActions = listOf(OPENED, SYNCHRONIZE, REOPENED)
    override fun handleEvent(event: PullRequestGitHubEvent) {
        when (PullRequestAction.of(event.action)) {
            OPENED, REOPENED, SYNCHRONIZE -> putIntoCache(event)
            CLOSED -> removeFromCache(event)
            else -> logger.debug("Skip pull request event with action {}", event.action)
        }
    }

    private fun removeFromCache(event: PullRequestGitHubEvent) {
        commitIdToPullRequestsCache.values().removeIf { it == event.toPullRequestInformation() }
    }

    private fun putIntoCache(event: PullRequestGitHubEvent) {
        commitIdToPullRequestsCache.put(event.getHeadSha(), event.toPullRequestInformation())
    }

    fun getPullRequests(commitId: String) = commitIdToPullRequestsCache.get(commitId)
}

fun PullRequestGitHubEvent.isFork() = pullRequest.head.repo.fork

fun PullRequestGitHubEvent.getSrcBranch() =
    if (isFork()) {
        // "myusername:my-branch-name"
        pullRequest.head.label
    } else {
        // my-branch-name
        pullRequest.head.ref
    }

fun PullRequestGitHubEvent.toPullRequestInformation() = PullRequestInformation(getSrcBranch(), getRepoFullName(), number)

data class PullRequestInformation(val branch: String, val repoName: String, val number: Long)
