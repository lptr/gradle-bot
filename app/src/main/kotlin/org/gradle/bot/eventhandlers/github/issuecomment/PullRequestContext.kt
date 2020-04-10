package org.gradle.bot.eventhandlers.github.issuecomment

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.Future
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.logger
import org.gradle.bot.model.AuthorAssociation
import org.gradle.bot.model.BuildStage
import org.gradle.bot.model.CommitStatusState.FAILURE
import org.gradle.bot.model.CommitStatusState.PENDING
import org.gradle.bot.model.PullRequestWithCommentsResponse
import org.gradle.bot.objectMapper
import org.jetbrains.teamcity.rest.Build

fun PullRequestWithCommentsResponse.getRepoName() = data.repository.nameWithOwner

fun PullRequestWithCommentsResponse.getBranchName() =
    if (data.repository.pullRequest.headRef.repository.isFork) {
        "pull/${data.repository.pullRequest.number}/head"
    } else {
        data.repository.pullRequest.headRef.name
    }

fun PullRequestWithCommentsResponse.getSubjectId() = data.repository.pullRequest.id
fun PullRequestWithCommentsResponse.getHeadRefSha() = data.repository.pullRequest.headRef.target.oid

fun PullRequestWithCommentsResponse.getComments(botName: String): List<PullRequestComment> {
    return data.repository.pullRequest.comments.nodes
        .map { comment ->
            val metadata = CommentMetadata.parseComment(comment.body)
            when {
                comment.author.login == botName && metadata.replyTargetCommentId != null -> BotReplyCommandComment(comment.databaseId, comment.body, metadata)
                comment.author.login == botName -> BotNotificationComment(comment.databaseId, comment.body, metadata)
                comment.body.contains("@$botName") -> CommandComment(comment.databaseId, comment.body, metadata, AuthorAssociation.isAdmin(comment.authorAssociation))
                else -> UnrelatedComment(comment.databaseId, comment.body, metadata)
            }.also {
                logger.info("Parse comment {} to {}", comment.body, it)
            }
        }
}

class PullRequestContext(
    private val gitHubClient: GitHubClient,
    private val teamCityClient: TeamCityClient,
    private val pr: PullRequestWithCommentsResponse
) {
    val comments: List<PullRequestComment> = pr.getComments(gitHubClient.whoAmI())
    fun executeCommentCommand(commentId: Long) {
        val targetComment = comments.find { it.id == commentId }
        if (targetComment == null) {
            logger.warn("Comment with id {} not found, skip.", commentId)
            return
        }
        if (alreadyReplied(targetComment)) {
            logger.warn("Comment {} has already been replied, skip.", targetComment.body)
        } else {
            targetComment.executeCommand(this)
        }
    }

    private fun alreadyReplied(targetComment: PullRequestComment): Boolean {
        return comments.any { targetComment.id == it.metadata.replyTargetCommentId }
    }

    fun reply(targetComment: PullRequestComment, content: String, teamCityBuildId: String? = null) {
        reply("""<!-- ${objectMapper.writeValueAsString(CommentMetadata(targetComment.id, teamCityBuildId, pr.getHeadRefSha()))} -->
            
$content""")
    }

    private fun reply(content: String) {
        gitHubClient.comment(pr.getSubjectId(), content).onSuccess {
            logger.info("Successfully commented $content on ${pr.getSubjectId()}")
        }
    }

    fun triggerBuild(targetStage: BuildStage) = teamCityClient.triggerBuild(targetStage, pr.getBranchName())

    fun findLatestTriggeredBuild(): Future<Build?> {
        val commentWithBuildId = comments.findLast { it.metadata.teamCityBuildId != null }
        return if (commentWithBuildId == null) {
            Future.succeededFuture(null)
        } else {
            teamCityClient.findBuild(commentWithBuildId.metadata.teamCityBuildId!!)
        }
    }

    /**
     * Only update pending statuses for failure statuses.
     *
     * For example, user triggers a build, all statuses will be updated to pending.
     *
     * Then early steps of the builds succeeds, late steps fails.
     *
     * User thinks it's not his fault, so he triggers a new build again.
     *
     * Now the early successful steps will not be rerun by TeamCity, so we should not update them.
     */
    fun updatePendingStatuses(targetBuildStage: BuildStage) {
        val currentNonFailureStatuses: List<String> = pr.data
            .repository.pullRequest.commits.nodes
            .getOrNull(0)?.commit?.status?.contexts
            ?.filter { it.state != FAILURE.toString() }?.map { it.context } ?: emptyList()

        val dependencies = targetBuildStage.dependencies.toMutableList().also { it.removeAll(currentNonFailureStatuses) }

        gitHubClient.createCommitStatus(pr.getRepoName(), pr.getHeadRefSha(), dependencies, PENDING)
    }

    private
    fun iDontUnderstandWhatYouSaid() = """
Sorry I don't understand what you said, please type `@${gitHubClient.whoAmI()} help` to get help.
"""

    private fun helpMessage() = """Currently I support the following commands:
        
- `@${gitHubClient.whoAmI()} test {BuildStage}` to trigger a build
  - e.g. `@${gitHubClient.whoAmI()} test SanityCheck plz`
  - `SanityCheck`/`CompileAll`/`QuickFeedbackLinux`/`QuickFeedback`/`ReadyForMerge`/`ReadyForNightly`/`ReadyForRelease` are supported
  - `test this` means `test ReadyForMerge`
  - `SanityCheck` can be abbreviated as `SC`, `ReadyForMerge` as `RFM`, etc.
- `@${gitHubClient.whoAmI()} help` to display this message
"""

    private fun noPermissionMessage() = "Sorry but I'm afraid you're not allowed to do this."

    fun replyNoPermission(comment: PullRequestComment) {
        reply(comment, noPermissionMessage())
    }

    fun replyHelp(sourceComment: CommandComment) {
        reply(sourceComment, helpMessage())
    }

    fun replyDontUnderstand(sourceComment: PullRequestComment) {
        reply(sourceComment, iDontUnderstandWhatYouSaid())
    }
}

data class CommentMetadata(
    @JsonProperty("replyTargetCommentId") val replyTargetCommentId: Long?,
    @JsonProperty("teamCityBuildId") val teamCityBuildId: String?,
    @JsonProperty("teamCityBuildHeadRef") val teamCityBuildHeadRef: String?
) {
    companion object {
        // The comment metadata are hidden tag in the comment, for example
        // <!-- {"replyTargetCommentId":604882513,"teamCityBuildId":null,"teamCityBuildHeadRef":"d32d0b7e33660dfb1a94ae9eea8238c793a4243e"} -->
        private val commentMetadataPattern = "<!-- (.*) -->".toPattern()
        private val nullMetadata = CommentMetadata(null, null, null)

        fun parseComment(commentBody: String): CommentMetadata {
            val matcher = commentMetadataPattern.matcher(commentBody)
            return if (matcher.find()) {
                objectMapper.readValue(matcher.group(1), CommentMetadata::class.java)
            } else {
                nullMetadata
            }
        }
    }
}

/**
 * Represents a comment in a pull request.
 */
interface PullRequestComment {
    /**
     * The global database id of the comment
     */
    val id: Long

    /**
     * The comment body
     */
    val body: String

    /**
     * The command included in this comment
     */
    val command: PullRequestCommand
        get() = NullCommand(this)

    /**
     * The metadata of this comment, e.g. the replyTargetCommentId
     */
    val metadata: CommentMetadata

    fun executeCommand(context: PullRequestContext) {
        command.execute(context)
    }
}

class CommandComment(override val id: Long, override val body: String, override val metadata: CommentMetadata, val isAdmin: Boolean) : PullRequestComment {
    override val command by lazy {
        parseCommand(body)
    }

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

    override fun executeCommand(context: PullRequestContext) {
        if (isAdmin) {
            command.execute(context)
        } else {
            context.replyNoPermission(this)
        }
    }
}

class UnrelatedComment(override val id: Long, override val body: String, override val metadata: CommentMetadata) : PullRequestComment

class BotReplyCommandComment(override val id: Long, override val body: String, override val metadata: CommentMetadata) : PullRequestComment

class BotNotificationComment(override val id: Long, override val body: String, override val metadata: CommentMetadata) : PullRequestComment
