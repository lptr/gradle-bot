package org.gradle.bot.handler

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.gradle.bot.MainVerticle
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.model.AuthorAssociation
import org.gradle.bot.model.BuildStage
import org.gradle.bot.model.CommitStatusEvent
import org.gradle.bot.model.GitHubEvent
import org.gradle.bot.model.IssueCommentEvent
import org.gradle.bot.model.PullRequestEvent
import org.gradle.bot.model.PullRequestWithComments
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


interface PullRequestCommand {
    fun execute(context: PullRequestContext)

    val sourceComment: PullRequestComment
}

class TestCommand(private val targetStage: BuildStage, override val sourceComment: PullRequestComment) : PullRequestCommand {
    override fun execute(context: PullRequestContext) {
        context.triggerBuild(targetStage).onSuccess {
            context.reply(sourceComment, "OK, I've already triggered [$targetStage build](${it.getHomeUrl()}) for you.")
        }
    }
}

class UnknownCommand(override val sourceComment: PullRequestComment) : PullRequestCommand {
    override fun execute(context: PullRequestContext) {
    }
}

class TestAndMergeCommand

class StopCommand

class HelpCommand

class NoOpCommand(override val sourceComment: PullRequestComment) : PullRequestCommand {
    override fun execute(context: PullRequestContext) {}
}

interface PullRequestComment {
    val id: Int

    fun getCommand(): PullRequestCommand = NoOpCommand(this)
}


class AdminCommandComment(override val id: Int, commentBody: String) : PullRequestComment {
    private val adminCommand = parseCommand(commentBody)
    override fun getCommand() = adminCommand

    private fun parseCommand(commentBody: String): PullRequestCommand {
        val words = commentBody.split("\\s")
        val testIndex = words.indexOf("test")
        return when {
            testIndex == -1 -> UnknownCommand(this)
            testIndex == words.size - 1 -> UnknownCommand(this)
            BuildStage.parseTargetStage(words[testIndex + 1]) == null -> UnknownCommand(this)
            else -> TestCommand(BuildStage.parseTargetStage(words[testIndex + 1])!!, this)
        }
    }
}

class NonAdminCommandComment(override val id: Int) : PullRequestComment {
}

class UnrelatedComment(override val id: Int) : PullRequestComment {
}

class BotReplyAdminCommandComment(override val id: Int, commentBody: String, metadata: CommentMetadata) : PullRequestComment {
}

class BotNotificationComment(override val id: Int, commentBody: String) : PullRequestComment {

}

class PullRequestContext(private val gitHubClient: GitHubClient,
                         private val teamCityClient: TeamCityClient,
                         private val comments: List<PullRequestComment>,
                         private val branchName: String,
                         private val subjectId: String) {
    fun processCommand(commentId: Int) {
        comments.find { it.id == commentId }?.getCommand()?.execute(this)
    }

    fun reply(targetComment: PullRequestComment, content: String) {
        reply("""
            <!-- ${objectMapper.writeValueAsString(CommentMetadata(targetComment.id))} -->
            $content
        """.trimIndent())
    }

    fun reply(content: String) {
        gitHubClient.comment(subjectId, content).onSuccess {
            logger.info("Successfully commented $content")
        }
    }

    fun triggerBuild(targetStage: BuildStage) = teamCityClient.triggerBuild(targetStage, branchName)
}

val myself = "blindpirate"

val commentMetadataPattern = "<!-- (.*) -->".toPattern()

data class CommentMetadata(@JsonProperty("replyTargetCommentId") val replyTargetCommentId: Int?) {
}

// <!-- {"replyTargetCommentId": 123455} -->
fun getCommentMetadata(commentBody: String): CommentMetadata? {
    val matcher = commentMetadataPattern.matcher(commentBody)
    return if (matcher.find()) {
        objectMapper.readValue(matcher.group(1), CommentMetadata::class.java)
    } else {
        null
    }
}

fun PullRequestWithComments.getComments(): List<PullRequestComment> {
    return data.repository.pullRequest.comments.nodes
            .map {
                val metadata = getCommentMetadata(it.body)
                when {
                    it.author.login == myself && metadata?.replyTargetCommentId != null -> BotReplyAdminCommandComment(it.databaseId, it.body, metadata)
                    it.author.login == myself -> BotNotificationComment(it.databaseId, it.body)
                    AuthorAssociation.isAdmin(it.authorAssociation) && it.body.contains("@$myself") -> AdminCommandComment(it.databaseId, it.body)
                    it.body.contains("@$myself") -> NonAdminCommandComment(it.databaseId)
                    else -> UnrelatedComment(it.databaseId)
                }
            }
}

@Singleton
class IssueCommentEventHandler @Inject constructor(private val gitHubClient: GitHubClient,
                                                   private val teamCityClient: TeamCityClient
) : GitHubEventHandler<IssueCommentEvent> {
    override fun handle(event: IssueCommentEvent) {
        gitHubClient.getPullRequestWithComments(event.repository.fullName, event.issue.number).onSuccess {
            val context = PullRequestContext(
                    gitHubClient, teamCityClient, it.getComments(),
                    it.data.repository.pullRequest.headRef.name,
                    event.issue.nodeId)
            context.processCommand(event.issue.id)
        }
    }
}

@Singleton
class GitHubWebHookHandler @Inject constructor(
//        private val eventHandlers: List<GitHubEventHandler<*>>,
                                               private val issueCommentEventHandler: IssueCommentEventHandler) : Handler<RoutingContext> {
    override fun handle(context: RoutingContext?) {
        logger.info("Received webhook to ${GitHubWebHookHandler::class.java.simpleName}")

        context.parsePayloadEvent()?.apply {
            if (this is IssueCommentEvent) {
                issueCommentEventHandler.handle(this)
            }
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
