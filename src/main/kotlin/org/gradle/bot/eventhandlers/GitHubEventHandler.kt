package org.gradle.bot.eventhandlers

import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import org.gradle.bot.PullRequestContext
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.getComments
import org.gradle.bot.model.GitHubEvent
import org.gradle.bot.model.IssueCommentGitHubEvent
import java.lang.reflect.ParameterizedType
import javax.inject.Inject
import javax.inject.Singleton

interface GitHubEventHandler<T : GitHubEvent> : Handler<Message<T>> {
    fun eventType(): String
}

@Singleton
class IssueCommentEventHandler @Inject constructor(private val gitHubClient: GitHubClient,
                                                   private val teamCityClient: TeamCityClient
) : AbstractGitHubEventHandler<IssueCommentGitHubEvent>() {
    override fun handle(eventMessage: Message<IssueCommentGitHubEvent>) {
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
        val klass = (superClass as ParameterizedType).actualTypeArguments[0] as Class<T>
        GitHubEvent.EVENT_TYPES.inverse().getValue(klass)
    }

    override fun eventType(): String = eventType
}