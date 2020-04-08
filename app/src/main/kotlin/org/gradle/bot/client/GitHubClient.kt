package org.gradle.bot.client

import io.vertx.core.Future
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.WebClient
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
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

@Singleton
open class GitHubClient @Inject constructor(
    @Named("GITHUB_ACCESS_TOKEN") githubToken: String,
    private val client: WebClient
) {
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

    fun whoAmI(): String = myself ?: throw IllegalStateException("Not initialized!")

    fun comment(subjectId: String, commentBody: String): Future<*> = doQueryOrMutation(Map::class.java, addCommentMutation(subjectId, commentBody))

    fun getPullRequestWithComments(repo: String, number: Long): Future<PullRequestWithCommentsResponse> =
        repo.split('/').let {
            pullRequestsWithCommentsQuery(it[0], it[1], number)
        }.let {
            doQueryOrMutation(PullRequestWithCommentsResponse::class.java, it)
        }

    fun listOpenPullRequests(repoName: String) =
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

    fun createCommitStatus(repoName: String, sha: String, state: CommitStatusState, statusUrl: String, desc: String, context: String) {
        val url = repoName.split('/').let { "https://api.github.com/repos/${it[0]}/${it[1]}/statuses/$sha" }
        client.postAbs(url)
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

    fun createCommitStatus(repoName: String, sha: String, dependencies: List<String>, statusState: CommitStatusState) {
        // https://developer.github.com/v3/repos/statuses/
        val url = repoName.split('/').let { "https://api.github.com/repos/${it[0]}/${it[1]}/statuses/$sha" }
        dependencies.forEach {
            client.postAbs(url)
                .putHeader("Accept", "application/json")
                .putHeader("Content-Type", "application/json")
                .putHeader("Authorization", authHeader)
                .sendBuffer(Buffer.buffer(
                    objectMapper.writeValueAsString(CommitStatusObject(statusState.name.toLowerCase(),
                        "https://builds.gradle.org",
                        "TeamCity build finished",
                        it)))
                ).onFailure {
                    logger.error("Error when creating commit status {} to {}", statusState, url)
                }.onSuccess {
                    logger.info("Get response: {}", it.bodyAsString())
                    logger.info("Successfully created commit status {} to {}", statusState, url)
                }
        }
    }
}
