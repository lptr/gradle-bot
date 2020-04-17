package org.gradle.bot.eventhandlers.github.issuecomment

import com.fasterxml.jackson.annotation.JsonProperty
import org.gradle.bot.model.CommitStatusState
import org.gradle.bot.objectMapper

// class PullRequestContext(
//    private val gitHubClient: GitHubClient,
//    private val teamCityClient: TeamCityClient,
//    private val pr: PullRequestWithCommentsResponse
// ) {
//    val comments: List<PullRequestComment> = pr.getComments(gitHubClient.whoAmI())
//    fun executeCommentCommand(commentId: Long) {
//        val targetComment = comments.find { it.id == commentId }
//        if (targetComment == null) {
//            logger.warn("Comment with id {} not found, skip.", commentId)
//            return
//        }
//        if (alreadyReplied(targetComment)) {
//            logger.warn("Comment {} has already been replied, skip.", targetComment.body)
//        } else {
//            targetComment.executeCommand(this)
//        }
//    }
//
//    private fun alreadyReplied(targetComment: PullRequestComment): Boolean {
//        return comments.any { targetComment.id == it.metadata.replyTargetCommentId }
//    }
//
//    fun reply(targetComment: PullRequestComment, content: String, teamCityBuildId: String? = null) {
//        reply("""<!-- ${objectMapper.writeValueAsString(CommentMetadata(targetComment.id, teamCityBuildId, pr.getHeadRefSha()))} -->
//
// $content""")
//    }
//
//    private fun reply(content: String) {
//        gitHubClient.comment(pr.getSubjectId(), content).onSuccess {
//            logger.info("Successfully commented $content on ${pr.getSubjectId()}")
//        }
//    }
//
//    fun triggerBuild(targetStage: BuildStage) = teamCityClient.triggerBuild(targetStage, pr.getBranchName())
//
//    fun findLatestTriggeredBuild(): Future<Build?> {
//        val commentWithBuildId = comments.findLast { it.metadata.teamCityBuildId != null }
//        return if (commentWithBuildId == null) {
//            Future.succeededFuture(null)
//        } else {
//            teamCityClient.findBuild(commentWithBuildId.metadata.teamCityBuildId!!)
//        }
//    }
//
//
//    /**
//     * Only update pending statuses for failure statuses.
//     *
//     * For example, user triggers a build, all statuses will be updated to pending.
//     *
//     * Then early steps of the builds succeeds, late steps fails.
//     *
//     * User thinks it's not his fault, so he triggers a new build again.
//     *
//     * Now the early successful steps will not be rerun by TeamCity, so we should not update them.
//     */
//    fun updatePendingStatuses(build: Build, targetBuildStage: BuildStage) {
//        val buildConfigurationIdToBuildMap = build.getAllDependencies().map { it.buildConfigurationId.stringId to it }.toMap()
//        val currentNonFailureStatuses: List<String> = pr.data
//            .repository.pullRequest.commits.nodes
//            .getOrNull(0)?.commit?.status?.contexts
//            ?.filter { it.state != FAILURE.toString() && it.state != ERROR.toString() }?.map { it.context } ?: emptyList()
//
//        val dependencies = targetBuildStage.dependencies.toMutableList()
//        dependencies.removeIf { currentNonFailureStatuses.contains(it.configName) }
//
//        val commitStatuses: List<CommitStatusObject> = dependencies.map {
//            CommitStatusObject(
//                PENDING.name.toLowerCase(),
//                buildConfigurationIdToBuildMap.get(it.id)?.getHomeUrl(),
//                "TeamCity build running",
//                it.configName
//            )
//        }
//
//        gitHubClient.createCommitStatus(pr.getRepoName(), pr.getHeadRefSha(), commitStatuses)
//    }
//
//    private
//    fun iDontUnderstandWhatYouSaid() = """
// Sorry I don't understand what you said, please type `@${gitHubClient.whoAmI()} help` to get help.
// """
//
//    private fun helpMessage() = """Currently I support the following commands:
//
// - `@${gitHubClient.whoAmI()} test {BuildStage}` to trigger a build
//  - e.g. `@${gitHubClient.whoAmI()} test SanityCheck plz`
//  - `SanityCheck`/`CompileAll`/`QuickFeedbackLinux`/`QuickFeedback`/`ReadyForMerge`/`ReadyForNightly`/`ReadyForRelease` are supported
//  - `test this` means `test ReadyForMerge`
//  - `SanityCheck` can be abbreviated as `SC`, `ReadyForMerge` as `RFM`, etc.
// - `@${gitHubClient.whoAmI()} help` to display this message
// """
//
//    private fun noPermissionMessage() = "Sorry but I'm afraid you're not allowed to do this."
//
//    fun replyNoPermission(comment: PullRequestComment) = reply(comment, noPermissionMessage())
//
//    fun replyHelp(sourceComment: CommandComment) = reply(sourceComment, helpMessage())
//
//    fun replyDontUnderstand(sourceComment: PullRequestComment) = reply(sourceComment, iDontUnderstandWhatYouSaid())
//
//    fun cancelPreviouslyTriggeredBuild(build: Build): CompositeFuture =
//        CompositeFuture.all(build.getAllDependencies().map {
//            teamCityClient.cancelBuild(build, "The user triggered a new build.")
//        })
// }
//
data class CommentMetadata(
    // The id of target comment which this comment replies
    @JsonProperty("replyTargetCommentId") val replyTargetCommentId: Long?,
    // The TC build id this comment triggers
    @JsonProperty("teamCityBuildId") val teamCityBuildId: String?,
    // The PR head commit when this comment triggers build
    @JsonProperty("teamCityBuildHeadRef") val teamCityBuildHeadRef: String?,
    // The build status. Will be updated by TeamCity webhook
    @JsonProperty("teamCityBuildStatus") val teamCityBuildStatus: CommitStatusState?,
    // The approval review id which this comment replies
    @JsonProperty("replyApprovalReviewId") val replyApproveReviewId: Long?
) {
    companion object {
        // The comment metadata are hidden tag in the comment, for example
        // <!-- {"replyTargetCommentId":604882513,"teamCityBuildId":null,"teamCityBuildHeadRef":"d32d0b7e33660dfb1a94ae9eea8238c793a4243e"} -->
        private val commentMetadataPattern = "<!-- (.*) -->".toPattern()
        private val nullMetadata = CommentMetadata(null, null, null, null, null)

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
//
