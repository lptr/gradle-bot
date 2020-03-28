package org.gradle.bot.webhookhandlers

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import org.gradle.bot.endWithJson
import org.gradle.bot.eventhandlers.IssueCommentEvent
import org.gradle.bot.eventhandlers.PullRequestEvent
import org.gradle.bot.model.CommitStatusEvent
import org.gradle.bot.model.GitHubEvent
import org.gradle.bot.objectMapper
import org.gradle.bot.security.GithubSignatureChecker
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

private val logger: Logger = LoggerFactory.getLogger(GitHubWebHookHandler::class.java.name)


@Singleton
class GitHubWebHookHandler @Inject constructor(
        private val vertx: Vertx,
        private val githubSignatureChecker: GithubSignatureChecker
) : Handler<RoutingContext> {
    override fun handle(context: RoutingContext?) {
        logger.info("Received webhook to ${GitHubWebHookHandler::class.java.simpleName}")

        context.parsePayloadEvent(githubSignatureChecker)?.apply {
            logger.debug("Start handling $this")
            vertx.eventBus().publish(this.javaClass.name, this)
        } ?: logger.info("Received invalid GitHub webhook, discard.")

        context?.response()?.endWithJson(emptyMap<String, Any>())
    }
}

val eventTypeToEventClassMap = mapOf(
        "status" to CommitStatusEvent::class.java,
        "pull_request" to PullRequestEvent::class.java,
        "issue_comment" to IssueCommentEvent::class.java
)

private fun RoutingContext?.parsePayloadEvent(githubSignatureChecker: GithubSignatureChecker): GitHubEvent? {
    return this?.let {
        val signature = request().getHeader("x-hub-signature")
        if (!githubSignatureChecker.verifySignature(bodyAsString, signature)) {
            logger.warn("Receive request {} with bad signature {}", bodyAsString, signature)
            return null
        }
    }?.let {
        logger.debug("Get GitHub webhook {}", bodyAsString)
        request().getHeader("X-GitHub-Event")
    }?.let {
        eventTypeToEventClassMap[it]
    }?.let {
        objectMapper.readValue(bodyAsString, it)
    }
}

