package org.gradle.bot

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.Future
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.model.AuthorAssociation
import org.gradle.bot.model.BuildStage
import org.gradle.bot.model.CommitStatus
import org.gradle.bot.model.PullRequestWithComments
import org.jetbrains.teamcity.rest.Build

interface PullRequestComment {
    val id: Int

    val body: String

    fun getCommand(): PullRequestCommand = NoOpCommand(this)

    fun getMetadata(): CommentMetadata = CommentMetadata(null, null, null)
}

class AdminCommandComment(override val id: Int, override val body: String, private val commentMetadata: CommentMetadata?) : PullRequestComment {
    private val adminCommand = parseCommand(body)

    override fun getMetadata(): CommentMetadata = commentMetadata ?: super.getMetadata()

    override fun getCommand() = adminCommand

    private fun parseCommand(commentBody: String): PullRequestCommand {
        val words = commentBody.split("\\s+".toRegex())
        val testIndex = words.indexOf("test")
        val helpIndex = words.indexOf("help")
        return when {
            helpIndex != -1 -> HelpCommand(this)
            testIndex == -1 -> UnknownCommand(this)
            testIndex == words.size - 1 -> UnknownCommand(this)
            BuildStage.parseTargetStage(words[testIndex + 1]) == null -> UnknownCommand(this)
            else -> TestCommand(BuildStage.parseTargetStage(words[testIndex + 1])!!, this)
        }.also {
            logger.info("Parsing comment {} to command {}", commentBody, it)
        }
    }
}

class NonAdminCommandComment(override val id: Int, override val body: String) : PullRequestComment {
}

class UnrelatedComment(override val id: Int, override val body: String) : PullRequestComment {
}

class BotReplyAdminCommandComment(override val id: Int, override val body: String, private val commentMetadata: CommentMetadata?) : PullRequestComment {
    override fun getMetadata(): CommentMetadata = commentMetadata ?: super.getMetadata()
}

class BotNotificationComment(override val id: Int, override val body: String) : PullRequestComment {
}

class PullRequestContext(private val gitHubClient: GitHubClient,
                         private val teamCityClient: TeamCityClient,
                         private val comments: List<PullRequestComment>,
                         private val repoName: String,
                         private val branchName: String,
                         private val subjectId: String,
                         private val headRefSha: String) {
    fun processCommand(commentId: Int) {
        val targetComment = comments.find { it.id == commentId }
        if (targetComment == null) {
            logger.warn("Comment with id {} not found, skip.", commentId)
        }
        if (alreadyReplied(targetComment!!)) {
            logger.warn("Comment {} has already been replied, skip.", targetComment.body)
        } else {
            targetComment.getCommand().execute(this)
        }
    }

    private fun alreadyReplied(targetComment: PullRequestComment): Boolean {
        return comments.any { targetComment.id == it.getMetadata().replyTargetCommentId }
    }

    fun reply(targetComment: PullRequestComment, content: String, teamCityBuildId: String? = null) {
        reply("""<!-- ${objectMapper.writeValueAsString(CommentMetadata(targetComment.id, teamCityBuildId, headRefSha))} -->
            $content
        """.trimIndent())
    }

    fun reply(content: String) {
        gitHubClient.comment(subjectId, content).onSuccess {
            logger.info("Successfully commented $content on $subjectId")
        }
    }

    fun triggerBuild(targetStage: BuildStage) = teamCityClient.triggerBuild(targetStage, branchName)

    fun findLatestTriggeredBuild(): Future<Build?> {
        val commentWithBuildId = comments.findLast { it.getMetadata().teamCityBuildId != null }
        return if (commentWithBuildId == null) {
            Future.succeededFuture(null)
        } else {
            teamCityClient.findBuild(commentWithBuildId.getMetadata().teamCityBuildId!!)
        }
    }

    fun publishPendingStatuses(dependencies: List<String>) {
        gitHubClient.createCommitStatus(repoName, headRefSha, dependencies, CommitStatus.PENDING)
    }

    fun iDontUnderstandWhatYouSaid()  = """
Sorry I don't understand what you said, please type `@${gitHubClient.whoAmI()} help` to get help.
"""

    fun helpMessage() = """Currently I support the following commands:
        
- `@${gitHubClient.whoAmI()} test {BuildStage} plz`, e.g. `${gitHubClient.whoAmI()} test SanityCheck plz`
  - {BuildStage} can be `SanityCheck`/`CompileAll`/QuickFeedbackLinux`/`QuickFeedback`/`ReadyForMerge`/`ReadyForNightly`/`ReadyForRelease`
  - Note that you can abbreviate "SanityCheck" as "SC", "ReadyForMerge" as "RFM", etc.
- `@${gitHubClient.whoAmI()} help` to display this message
"""
}

val commentMetadataPattern = "<!-- (.*) -->".toPattern()

data class CommentMetadata(
        @JsonProperty("replyTargetCommentId") val replyTargetCommentId: Int?,
        @JsonProperty("teamCityBuildId") val teamCityBuildId: String?,
        @JsonProperty("teamCityBuildHeadRef") val teamCityBuildHeadRef: String?
) {
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

fun PullRequestWithComments.getComments(myself: String): List<PullRequestComment> {
    return data.repository.pullRequest.comments.nodes
            .map { comment ->
                val metadata = getCommentMetadata(comment.body)
                when {
                    comment.author.login == myself && metadata?.replyTargetCommentId != null -> BotReplyAdminCommandComment(comment.databaseId, comment.body, metadata)
//                    comment.author.login == myself -> BotNotificationComment(comment.databaseId, comment.body)
                    AuthorAssociation.isAdmin(comment.authorAssociation) && comment.body.contains("@$myself") -> AdminCommandComment(comment.databaseId, comment.body, metadata)
                    comment.body.contains("@$myself") -> NonAdminCommandComment(comment.databaseId, comment.body)
                    else -> UnrelatedComment(comment.databaseId, comment.body)
                }.also {
                    logger.info("Parse comment {} to {}", comment.body, it)
                }
            }
}
