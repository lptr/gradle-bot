package org.gradle.bot.handler

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.gradle.bot.PullRequestContext
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.endWithJson
import org.gradle.bot.getComments
import org.gradle.bot.model.CommitStatusEvent
import org.gradle.bot.model.GitHubEvent
import org.gradle.bot.model.IssueCommentEvent
import org.gradle.bot.model.PullRequestEvent
import org.gradle.bot.objectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.ParameterizedType
import javax.inject.Inject
import javax.inject.Singleton

private val logger: Logger = LoggerFactory.getLogger(GitHubWebHookHandler::class.java.name)

interface GitHubEventHandler<T : GitHubEvent> {

    fun accept(event: GitHubEvent): Boolean

    fun handle(event: T)
}

abstract class AbstractGitHubEventHandler<T : GitHubEvent> : GitHubEventHandler<T> {
    private val supportEventType by lazy {
        val superClass = javaClass.genericSuperclass
        (superClass as ParameterizedType).actualTypeArguments[0] as Class<T>
    }

    override fun accept(event: GitHubEvent) = supportEventType.isAssignableFrom(event.javaClass)
}

@Singleton
class GitHubEventHandlerComposite @Inject constructor(private val issueCommentEventHandler: IssueCommentEventHandler) :
        GitHubEventHandler<GitHubEvent> {
    @Suppress("UNCHECKED_CAST")
    private val eventHandlers = listOf(issueCommentEventHandler as GitHubEventHandler<GitHubEvent>)

    override fun accept(event: GitHubEvent) = throw UnsupportedOperationException()

    override fun handle(event: GitHubEvent) {
        eventHandlers.forEach {
            if (it.accept(event)) {
                try {
                    it.handle(event)
                } catch (e: Throwable) {
                    logger.error("Error handling event $event", e)
                }
            }
        }
    }
}

@Singleton
class IssueCommentEventHandler @Inject constructor(private val gitHubClient: GitHubClient,
                                                   private val teamCityClient: TeamCityClient
) : AbstractGitHubEventHandler<IssueCommentEvent>() {
    override fun handle(event: IssueCommentEvent) {
        gitHubClient.getPullRequestWithComments(event.repository.fullName, event.issue.number).onSuccess {
            val context = PullRequestContext(
                    gitHubClient, teamCityClient, it.getComments(gitHubClient.whoAmI()),
                    it.data.repository.pullRequest.headRef.name,
                    event.issue.nodeId,
                    event.repository.fullName,
                    it.data.repository.pullRequest.headRef.target.oid)
            context.processCommand(event.comment.id)
        }
    }
}

@Singleton
class GitHubWebHookHandler @Inject constructor(
        private val gitHubEventHandler: GitHubEventHandlerComposite) : Handler<RoutingContext> {
    override fun handle(context: RoutingContext?) {
        logger.info("Received webhook to ${GitHubWebHookHandler::class.java.simpleName}")

        context.parsePayloadEvent()?.apply {
            logger.debug("Start handling $this")
            gitHubEventHandler.handle(this)
        } ?: logger.info("Received invalid GitHub webhook, discard.")

        context?.response()?.endWithJson(emptyMap<String, Object>())
    }
}

val eventTypeToEventClassMap = mapOf(
        "status" to CommitStatusEvent::class.java,
        "pull_request" to PullRequestEvent::class.java,
        "issue_comment" to IssueCommentEvent::class.java
)

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
