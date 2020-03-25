package org.gradle.bot.handler

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.gradle.bot.MainVerticle
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.model.CommitStatusEvent
import org.gradle.bot.model.GitHubEvent
import org.gradle.bot.model.IssueCommentEvent
import org.gradle.bot.model.PullRequestEvent
import org.gradle.bot.model.PullRequestsWithComments
import org.gradle.bot.objectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

val logger: Logger = LoggerFactory.getLogger(MainVerticle::class.java.name)


val eventTypeToEventClassMap = mapOf(
        "status" to CommitStatusEvent::class.java,
        "pull_request" to PullRequestEvent::class.java,
        "issue_comment" to IssueCommentEvent::class.java
)

interface GitHubEventHandler<T : GitHubEvent> {
    fun handle(event: T)
}

enum class BuildStage(val fullName: String, val abbr: String) {
    COMPILE_ALL("CompileAll", "CA"),
    SANITY_CHECK("SanityCheck", "SC"),
    QUICK_FEEDBACK_LINUX("QuickFeedbackLinux", "QFL"),
    QUICK_FEEDBACK("QuickFeedback", "QF"),
    READY_FOR_MERGE("ReadyForMerge", "RFM"),
    READY_FOR_NIGHTLY("ReadyForNightly", "RFN"),
    READY_FOR_RELEASE("ReadyForRelease", "RFR")
}

class TestCommand

class TestAndMergeCommand

class StopCommand

class HelpCommand

fun PullRequestsWithComments.getCommand() {

}

@Singleton
class IssueCommentEventHandler @Inject constructor(private val gitHubClient: GitHubClient) : GitHubEventHandler<IssueCommentEvent> {
    override fun handle(event: IssueCommentEvent) {
        gitHubClient.getPullRequestWithComments(event.repository.fullName, event.issue.number).onSuccess {
        }
    }
}

@Singleton
class GitHubWebHookHandler @Inject constructor(private val eventHandlers: List<GitHubEventHandler<*>>) : Handler<RoutingContext> {
    override fun handle(context: RoutingContext?) {
        logger.info("Received webhook to ${GitHubWebHookHandler::class.java.simpleName}")

        context.parsePayloadEvent()?.apply {
        } ?: logger.info("Received invalid GitHub webhook, discard.")
    }
}

private fun RoutingContext?.parsePayloadEvent(): GitHubEvent? {
    return this?.let {
        logger.debug("Get GitHub webhook {}", bodyAsString)
        request().getHeader("X-GitHub-Event")
    }?.let {
        eventTypeToEventClassMap[it]
    }?.let {
        objectMapper.readValue(bodyAsString, it)
    }
}

private fun RoutingContext?.isValidGitHubWebHook(): Boolean {
    return if (this == null) {
        false
    } else {
        // https://developer.github.com/webhooks/
        request().getHeader("X-GitHub-Event") != null
                && bodyAsString != null
    }
}
