package org.gradle.bot.client

import com.google.inject.ImplementedBy
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import org.gradle.bot.eventhandlers.github.issuecomment.CommentMetadata
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestComment
import org.gradle.bot.eventhandlers.github.pullrequest.PullRequestReview
import org.gradle.bot.model.CommitStatusObject
import org.gradle.bot.model.CommitStatusState
import org.gradle.bot.model.ListOpenPullRequestsResponse
import org.gradle.bot.model.PullRequestWithCommentsResponse
import org.gradle.bot.model.WhoAmIResponse
import org.gradle.bot.model.addCommentMutation
import org.gradle.bot.model.listOpenPullRequestsQuery
import org.gradle.bot.model.pullRequestsWithCommentsQuery
import org.gradle.bot.model.whoAmIQuery
import org.gradle.bot.objectMapper
import org.slf4j.LoggerFactory

@ImplementedBy(DefaultGitHubClient::class)
interface GitHubClient {
    fun whoAmI(): String

    fun comment(subjectId: String, commentBody: String): Future<*>

    fun getPullRequestWithComments(repo: String, number: Long): Future<PullRequestWithCommentsResponse>

    fun listOpenPullRequests(repoName: String): Future<ListOpenPullRequestsResponse>

    fun createCommitStatus(repoName: String, sha: String, state: CommitStatusState, statusUrl: String, desc: String, context: String): Future<HttpResponse<Buffer>>

    fun createCommitStatus(repoName: String, sha: String, commitStatusObjects: List<CommitStatusObject>): CompositeFuture

    fun reply(targetComment: PullRequestComment, content: String, teamCityBuildId: String?)
    fun reply(targetComment: PullRequestComment, content: String)
    fun reply(targetReview: PullRequestReview, content: String)
}

@Singleton
open class DefaultGitHubClient @Inject constructor(
    @Named("GITHUB_ACCESS_TOKEN") githubToken: String,
    private val client: WebClient
) : GitHubClient {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val authHeader = "bearer $githubToken"

    private var myself: String? = null

    fun init(): Future<String> = doQueryOrMutation(WhoAmIResponse::class.java, whoAmIQuery).map {
        it.data.viewer.login
    }.onSuccess {
        myself = it
    }.onFailure {
        logger.error("Can't get bot authentication data", it)
    }

    override fun whoAmI(): String = myself ?: throw IllegalStateException("Not initialized!")

    override fun comment(subjectId: String, commentBody: String): Future<*> = doQueryOrMutation(Map::class.java, addCommentMutation(subjectId, commentBody))

    override fun getPullRequestWithComments(repo: String, number: Long): Future<PullRequestWithCommentsResponse> =
        repo.split('/').let {
            pullRequestsWithCommentsQuery(it[0], it[1], number)
        }.let {
            doQueryOrMutation(PullRequestWithCommentsResponse::class.java, it)
        }

    override fun listOpenPullRequests(repoName: String) =
        repoName.split('/').let {
            val query = listOpenPullRequestsQuery(it[0], it[1])
            doQueryOrMutation(ListOpenPullRequestsResponse::class.java, query)
        }

    private
    fun <T> doQueryOrMutation(klass: Class<T>, queryString: String): Future<T> = client.postAbs("https://api.github.com/graphql")
        .putHeader("Accept", "application/json")
        .putHeader("Content-Type", "application/json")
        .putHeader("Authorization", authHeader)
        .sendBuffer(Buffer.buffer(toQueryJson(queryString)))
        .map {
            val response = it.bodyAsString()
            logger.debug("Get response while querying {}\n{}\n{}", klass.simpleName, queryString, response)
            objectMapper.readValue(response, klass)
        }.onFailure {
            logger.error("Error while querying ${klass.simpleName}\n$queryString", it)
        }

    private
    fun toQueryJson(query: String) = objectMapper.writeValueAsString(mapOf("query" to query))

    override fun createCommitStatus(repoName: String, sha: String, state: CommitStatusState, statusUrl: String, desc: String, context: String): Future<HttpResponse<Buffer>> {
        val url = repoName.split('/').let { "https://api.github.com/repos/${it[0]}/${it[1]}/statuses/$sha" }
        return client.postAbs(url)
            .putHeader("Accept", "application/json")
            .putHeader("Content-Type", "application/json")
            .putHeader("Authorization", authHeader)
            .sendBuffer(Buffer.buffer(
                objectMapper.writeValueAsString(
                    CommitStatusObject(state.name.toLowerCase(), statusUrl, desc, context)))
            ).onFailure {
                logger.error("Error when creating commit status {} to {}", state, url)
            }.onSuccess {
                logger.info("Get response: {}", it.bodyAsString())
                logger.info("Successfully created commit status {} to {}", state, url)
            }
    }

    override fun createCommitStatus(repoName: String, sha: String, commitStatusObjects: List<CommitStatusObject>): CompositeFuture {
        // https://developer.github.com/v3/repos/statuses/
        val url = repoName.split('/').let { "https://api.github.com/repos/${it[0]}/${it[1]}/statuses/$sha" }
        return CompositeFuture.all(commitStatusObjects.map { commitStatus ->
            client.postAbs(url)
                .putHeader("Accept", "application/json")
                .putHeader("Content-Type", "application/json")
                .putHeader("Authorization", authHeader)
                .sendBuffer(Buffer.buffer(objectMapper.writeValueAsString(commitStatus)))
                .onFailure {
                    logger.error("Error when creating commit status {} to {}", commitStatus, url)
                }.onSuccess {
                    logger.info("Get response: {}", it.bodyAsString())
                    logger.info("Successfully created commit status {} to {}", commitStatus, url)
                }
        })
    }

    override fun reply(targetComment: PullRequestComment, content: String) {
        reply(targetComment, content, null)
    }

    override fun reply(targetReview: PullRequestReview, content: String) {
        val contentWithMetadata = """<!-- ${objectMapper.writeValueAsString(CommentMetadata(null, null, null, null, targetReview.id))} -->
            
$content"""
        comment(targetReview.pullRequest.subjectId, contentWithMetadata)
    }

    override fun reply(targetComment: PullRequestComment, content: String, teamCityBuildId: String?) {
        val contentWithMetadata = """<!-- ${objectMapper.writeValueAsString(CommentMetadata(targetComment.id, teamCityBuildId, targetComment.pullRequest.headCommitSha, CommitStatusState.PENDING, null))} -->
            
$content"""
        comment(targetComment.pullRequest.subjectId, contentWithMetadata)
    }
}
