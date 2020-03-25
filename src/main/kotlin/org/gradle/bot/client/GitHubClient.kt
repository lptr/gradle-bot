package org.gradle.bot.client

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.WebClient
import org.gradle.bot.model.PullRequestsWithComments
import org.gradle.bot.model.pullRequestsWithCommentsQuery
import org.gradle.bot.objectMapper
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubClient @Inject constructor(private val vertx: Vertx) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val client = WebClient.create(vertx)
    private val authHeader by lazy {
        val githubToken = System.getenv("GITHUB_ACCESS_TOKEN") ?: throw IllegalStateException("Must set env variable GITHUB_ACCESS_TOKEN")
        "bearer $githubToken"
    }

    fun getPullRequestWithComments(repo: String, number: Int): Future<PullRequestsWithComments> {
        val query = repo.split('/').let {
            pullRequestsWithCommentsQuery(it[0], it[1], number)
        }
        return client.postAbs("https://api.github.com/graphql")
                .putHeader("Accept", "application/json")
                .putHeader("Content-Type", "application/json")
                .putHeader("Authorization", authHeader)
                .sendBuffer(Buffer.buffer(toQueryJson(query)))
                .map {
                    val response = it.bodyAsString()
                    logger.debug("Get response while querying {}/{}:\n{}\n{}", repo, number, query, response)
                    objectMapper.readValue(response, PullRequestsWithComments::class.java)
                }.onFailure {
                    logger.error("Error while querying $repo/$number:\n $query", it)
                }
    }

    private
    fun toQueryJson(query: String) = objectMapper.writeValueAsString(mapOf("query" to query))
}