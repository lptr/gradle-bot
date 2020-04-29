package org.gradle.bot.eventhandlers.github.pullrequest

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject
import javax.inject.Singleton
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.eventhandlers.github.TestCommand
import org.gradle.bot.eventhandlers.github.helpMessage
import org.gradle.bot.eventhandlers.github.iDontUnderstandWhatYouSaid
import org.gradle.bot.eventhandlers.github.issuecomment.CommentMetadata
import org.gradle.bot.eventhandlers.github.noPermissionComment
import org.gradle.bot.eventhandlers.github.readyForMergeComment
import org.gradle.bot.logger
import org.gradle.bot.model.AuthorAssociation
import org.gradle.bot.model.BuildStage
import org.gradle.bot.model.CommitStatusObject
import org.gradle.bot.model.CommitStatusState
import org.gradle.bot.model.PullRequestWithCommentsResponse

fun parseComment(
    commentId: Long,
    commentAuthor: String,
    pullRequest: PullRequest,
    commentAuthorAssociation: String,
    commentBody: String,
    botName: String
): PullRequestComment {
    val metadata = CommentMetadata.parseComment(commentBody)
    return when {
        commentBody.contains("`@$botName") -> UnrelatedComment(commentId, commentBody, pullRequest, metadata)
        commentBody.contains("@$botName") -> CommandComment(commentId, commentBody, pullRequest, metadata, AuthorAssociation.isAdmin(commentAuthorAssociation) || commentAuthor == "bmuskalla")
        commentAuthor == botName && metadata.replyTargetCommentId != null -> BotReplyCommandComment(commentId, commentBody, pullRequest, metadata)
        commentAuthor == botName -> BotNotificationComment(commentId, commentBody, pullRequest, metadata)
        else -> UnrelatedComment(commentId, commentBody, pullRequest, metadata)
    }.also {
        logger.debug("Parse comment {} to {}", commentBody, it)
    }
}

data class PullRequestKey(val repoName: String, val number: Long)

@Singleton
class PullRequestManager @Inject constructor(
    private val gitHubClient: GitHubClient,
    private val teamCityClient: TeamCityClient
) {
    private val pullRequests: ConcurrentMap<PullRequestKey, PullRequest> = ConcurrentHashMap()

    /**
     * Pull request information will be fully updated upon issue_comment and pull_request.open/reopen/synchronize
     *
     * Necessary actions will be triggered, e.g. trigger build, reply comment, merge PR, etc.
     */
    fun update(newPr: PullRequest) = pullRequests.put(newPr.key, newPr)

    /**
     * Whether this build was triggered by the bot
     */
    fun findPullRequestByBuildId(buildId: String): PullRequest? = pullRequests.values.find { pr ->
        pr.comments.any { it.metadata.teamCityBuildId == buildId }
    }

    fun updateCIStatusIfNecessary(pr: PullRequest) {
        teamCityClient.getLatestFinishedBuild(pr.targetBranch).onSuccess { lastSuccessfulBuild ->
            val oldStatus = pr.commitStatuses.find { it.context == ciStatusContext }
            val commitStatusState = CommitStatusState.fromTeamCityBuildStatus(lastSuccessfulBuild.status!!)
            if (commitStatusState.toString() != oldStatus?.state) {
                gitHubClient.createCommitStatus(pr.repoName,
                    pr.headCommitSha,
                    commitStatusState,
                    lastSuccessfulBuild.getHomeUrl(),
                    ciStatusDesc(lastSuccessfulBuild, commitStatusState),
                    ciStatusContext)
            }
        }
    }

    fun updateCommitStatus(repoName: String, commitSha: String, commitStatus: CommitStatusObject) {
        pullRequests.values.forEach { updateCommitStatusForPullRequest(it, repoName, commitSha, commitStatus) }
    }

    private fun updateCommitStatusForPullRequest(pr: PullRequest, repoName: String, commitSha: String, commitStatus: CommitStatusObject) {
        if (pr.repoName != repoName || pr.headCommitSha != commitSha) {
            return
        }
        val oldCommitStatusIndex = pr.commitStatuses.indexOfFirst { it.context == commitStatus.context }
        if (oldCommitStatusIndex == -1) {
            pr.commitStatuses.add(commitStatus)
        } else {
            val oldCommitStatus = pr.commitStatuses[oldCommitStatusIndex]
            if (stateChangedToReadyForMerge(oldCommitStatus, commitStatus, pr) &&
                !approvalReviewHasNotBeenReplied(pr) &&
                authorLikesBot(pr)) {
                gitHubClient.reply(pr.reviews.last(), readyForMergeComment(pr.author))
            }
        }
    }

    private fun authorLikesBot(pr: PullRequest): Boolean {
        return pr.comments.any { it is CommandComment }
    }

    private fun approvalReviewHasNotBeenReplied(pr: PullRequest): Boolean {
        val repliedApprovalReviewId = pr.comments.flatMap {
            if (it.metadata.replyApproveReviewId != null) listOf(it.metadata.replyApproveReviewId) else emptyList()
        }

        val lastApprovalReviewId = pr.reviews.lastOrNull()?.id
        return repliedApprovalReviewId.contains(lastApprovalReviewId)
    }

    private fun stateChangedToReadyForMerge(oldCommitStatus: CommitStatusObject, newCommitStatus: CommitStatusObject, pr: PullRequest): Boolean {
        return oldCommitStatus.state != CommitStatusState.SUCCESS.toString() &&
            newCommitStatus.state == CommitStatusState.SUCCESS.toString() &&
            pr.isApproved
    }

    private fun alreadyReplied(pr: PullRequest, targetComment: PullRequestComment): Boolean {
        return pr.comments.any { targetComment.id == it.metadata.replyTargetCommentId }
    }

    fun onPullRequestCommentCreated(pullRequest: PullRequest, targetCommentId: Long) {
        val targetComment = pullRequest.comments.find { it.id == targetCommentId } ?: return
        if (alreadyReplied(pullRequest, targetComment)) {
            logger.warn("Comment {} has already been replied, skip.", targetComment.body)
        } else {
            targetComment.execute(PullRequestContext(gitHubClient, teamCityClient, this))
        }
    }
}

/**
 * PullRequestCommand is some commands that administrators send via pull request comments,
 * for example,
 *
 * <pre>
 * @bot-gradle test this please
 * @bot-gradle test and merge this please
 * </pre>
 */
interface PullRequestCommand {
    fun execute(context: PullRequestContext)
}

class UnknownCommand(val sourceComment: PullRequestComment) : PullRequestCommand {
    override fun execute(context: PullRequestContext) {
        context.reply(sourceComment, iDontUnderstandWhatYouSaid(context.whoAmI()))
    }
}

class HelpCommand(val sourceComment: CommandComment) : PullRequestCommand {
    override fun execute(context: PullRequestContext) {
        context.reply(sourceComment, helpMessage(context.whoAmI()))
    }
}

class NullCommand(val sourceComment: PullRequestComment) : PullRequestCommand {
    override fun execute(context: PullRequestContext) {}
}

class PullRequest(
    botName: String,
    pr: PullRequestWithCommentsResponse
) {
    // https://github.com/blindpirate/gradle-bot/issues/13
    // Sometimes when you open a pull request and immediately comment,
    // the comment is not visible in query response
    // In this case, we manually add it.
    fun addCommentIfNotExist(
        commentId: Long,
        commentAuthor: String,
        commentAuthorAssociation: String,
        commentBody: String,
        botName: String
    ) {
        if (comments.find { it.id == commentId } == null) {
            comments.add(parseComment(commentId, commentAuthor, this, commentAuthorAssociation, commentBody, botName))
        }
    }

    // gradle/gradle
    val repoName: String = pr.data.repository.nameWithOwner

    // 12345
    val number: Long = pr.data.repository.pullRequest.number

    val author: String = pr.data.repository.pullRequest.author.login

    val subjectId: String = pr.data.repository.pullRequest.id

    val key: PullRequestKey = PullRequestKey(repoName, number)

    val body: String = pr.data.repository.pullRequest.body

    val headCommitSha = pr.data.repository.pullRequest.headRef.target.oid

    val isFork = pr.data.repository.pullRequest.headRef.repository.isFork

    val isApproved
        get() = reviews.any { it.state == PullRequestReviewState.APPROVED }

    val reviews: MutableList<PullRequestReview> = pr.data.repository.pullRequest.reviews.nodes
        .map { PullRequestReview(it.databaseId, it.author.login, it.body, PullRequestReviewState.valueOf(it.state), this) }.toMutableList()

    val targetBranch = pr.data.repository.pullRequest.baseRefName

    val branchName = if (isFork) {
        // "myusername:my-branch-name"
        "${pr.data.repository.pullRequest.headRef.repository.owner.login}:${pr.data.repository.pullRequest.headRef.name}"
    } else {
        // my-branch-name
        pr.data.repository.pullRequest.headRef.name
    }

    val teamCityBranchName = if (isFork) {
        "pull/${pr.data.repository.pullRequest.number}/head"
    } else {
        pr.data.repository.pullRequest.headRef.name
    }

    val commitStatuses: MutableList<CommitStatusObject> = pr.data.repository.pullRequest.commits.nodes
        .getOrNull(0)?.commit?.status?.contexts?.map(PullRequestWithCommentsResponse.Context::toCommitStatus)?.toMutableList() ?: mutableListOf()

    val comments: MutableList<PullRequestComment> = pr.data.repository.pullRequest.comments.nodes
        .map { parseComment(it.databaseId, it.author.login, this, it.authorAssociation, it.body, botName) }.toMutableList()
}

// https://developer.github.com/v4/enum/pullrequestreviewstate/
enum class PullRequestReviewState {
    APPROVED, CHANGES_REQUESTED, COMMENTED, DISMISSED, PENDING
}

class PullRequestReview(val id: Long, val author: String, val body: String, val state: PullRequestReviewState, val pullRequest: PullRequest)

private fun PullRequestWithCommentsResponse.Context.toCommitStatus() = CommitStatusObject(state, targetUrl, description, context)

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
     * The metadata of this comment, e.g. the replyTargetCommentId
     */
    val metadata: CommentMetadata

    val pullRequest: PullRequest

    fun execute(context: PullRequestContext) {
    }
}

class PullRequestContext(
    val gitHubClient: GitHubClient,
    val teamCityClient: TeamCityClient,
    val pullRequestManager: PullRequestManager
) : GitHubClient by gitHubClient, TeamCityClient by teamCityClient

class CommandComment(
    override val id: Long,
    override val body: String,
    override val pullRequest: PullRequest,
    override val metadata: CommentMetadata,
    val isAdmin: Boolean
) : PullRequestComment {
    val command by lazy {
        parseCommand(body)
    }

    private fun parseCommand(commentBody: String): PullRequestCommand {
        val words = commentBody.replace("[^-@\\w]".toRegex(), " ").split("\\s+".toRegex())
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

    override fun execute(context: PullRequestContext) {
        if (isAdmin) {
            command.execute(context)
        } else {
            context.gitHubClient.reply(this, noPermissionComment)
        }
    }
}

class UnrelatedComment(
    override val id: Long,
    override val body: String,
    override val pullRequest: PullRequest,
    override val metadata: CommentMetadata
) : PullRequestComment

class BotReplyCommandComment(
    override val id: Long,
    override val body: String,
    override val pullRequest: PullRequest,
    override val metadata: CommentMetadata
) : PullRequestComment

class BotNotificationComment(
    override val id: Long,
    override val body: String,
    override val pullRequest: PullRequest,
    override val metadata: CommentMetadata
) : PullRequestComment
