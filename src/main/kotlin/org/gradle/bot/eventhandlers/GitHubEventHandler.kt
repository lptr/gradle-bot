package org.gradle.bot.eventhandlers

import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import org.gradle.bot.PullRequestContext
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.getComments
import org.gradle.bot.model.GitHubEvent
import org.gradle.bot.model.IssueCommentEvent
import java.lang.reflect.ParameterizedType
import javax.inject.Inject
import javax.inject.Singleton

interface GitHubEventHandler<T : GitHubEvent> : Handler<Message<T>> {
    fun eventType(): String
}

//@Singleton
//class GitHubEventHandlerComposite @Inject constructor(private val issueCommentEventHandler: IssueCommentEventHandler) :
//        GitHubEventHandler<GitHubEvent> {
//    @Suppress("UNCHECKED_CAST")
//    private val eventHandlers = listOf(issueCommentEventHandler as GitHubEventHandler<GitHubEvent>)
//
//    override fun accept(event: GitHubEvent) = throw UnsupportedOperationException()
//
//    override fun handle(event: GitHubEvent) {
//        eventHandlers.forEach {
//            if (it.accept(event)) {
//                try {
//                    it.handle(event)
//                } catch (e: Throwable) {
//                    logger.error("Error handling event $event", e)
//                }
//            }
//        }
//    }
//}

@Singleton
class IssueCommentEventHandler @Inject constructor(private val gitHubClient: GitHubClient,
                                                   private val teamCityClient: TeamCityClient
) : AbstractGitHubEventHandler<IssueCommentEvent>() {
    override fun handle(eventMessage: Message<IssueCommentEvent>) {
        val event = eventMessage.body()

        gitHubClient.getPullRequestWithComments(event.repository.fullName, event.issue.number).onSuccess {
            val context = PullRequestContext(
                    gitHubClient,
                    teamCityClient,
                    it.getComments(gitHubClient.whoAmI()),
                    event.repository.fullName,
                    it.data.repository.pullRequest.headRef.name,
                    event.issue.nodeId,
                    it.data.repository.pullRequest.headRef.target.oid)
            context.processCommand(event.comment.id)
        }
    }
}

abstract class AbstractGitHubEventHandler<T : GitHubEvent> : GitHubEventHandler<T> {
    private val eventType by lazy {
        val superClass = javaClass.genericSuperclass
        (superClass as ParameterizedType).actualTypeArguments[0] as Class<T>
    }

    override fun eventType(): String = eventType.name
}