package org.gradle.bot.handler

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.gradle.bot.MainVerticle
import org.gradle.bot.model.CommitStatusEvent
import org.gradle.bot.model.GitHubEvent
import org.gradle.bot.model.IssueCommentEvent
import org.gradle.bot.model.PullRequestEvent
import java.util.logging.Logger

val logger: Logger = Logger.getLogger(MainVerticle::class.java.name)

val objectMapper = ObjectMapper()

val eventTypeToEventClassMap = mapOf(
        "status" to CommitStatusEvent::class.java,
        "pull_request" to PullRequestEvent::class.java,
        "issue_comment" to IssueCommentEvent::class.java
)

class GitHubWebHookHandler : Handler<RoutingContext> {
    override fun handle(context: RoutingContext?) {
        logger.info("Received webhook to ${GitHubWebHookHandler::class.java.simpleName}")

        context.parsePayloadEvent()?.apply {
            println(this)
        } ?: logger.info("Received invalid GitHub webhook, discard.")
    }
}

private fun RoutingContext?.parsePayloadEvent(): GitHubEvent? {
    return this?.let {
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
