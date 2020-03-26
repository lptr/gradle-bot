package org.gradle.bot.client

import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.ProxyOptions
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import org.gradle.bot.model.PullRequestWithComments
import org.gradle.bot.model.pullRequestsWithCommentsQuery
import org.gradle.bot.objectMapper
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubClient @Inject constructor(vertx: Vertx) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val client = WebClient.create(vertx,
            WebClientOptions().also { options ->
                if (System.getProperty("http.proxyHost") != null && System.getProperty("http.proxyPort") != null) {
                    options.proxyOptions = ProxyOptions().also {
                        it.host = System.getProperty("http.proxyHost")
                        it.port = System.getProperty("http.proxyPort").toInt()
                    }
                }
            })
    private val authHeader by lazy {
        val githubToken = System.getenv("GITHUB_ACCESS_TOKEN") ?: throw IllegalStateException("Must set env variable GITHUB_ACCESS_TOKEN")
        "bearer $githubToken"
    }

    fun whoAmI() = "blindpirate"

    fun comment(subjectId: String, commentBody: String): Future<*> {
        val mutation = """
mutation {            
  addComment(input:{subjectId:"MDExOlB1bGxSZXF1ZXN0MzkzNjEwMjA2",body:"${commentBody.replace("\n", "\\n")}"}) {
    clientMutationId
    subject {
      id
    }
    commentEdge {
      node {
        databaseId
      }
    }
  }
}  
        """.replace('\n', ' ')

        return doQueryOrMutation(Map::class.java, mutation)
    }

    fun getPullRequestWithComments(repo: String, number: Int): Future<PullRequestWithComments> =
            repo.split('/').let {
                pullRequestsWithCommentsQuery(it[0], it[1], number)
            }.let {
                doQueryOrMutation(PullRequestWithComments::class.java, it)
            }

    private
    fun <T> doQueryOrMutation(klass: Class<T>, queryString: String): Future<T> {
        return client.postAbs("https://api.github.com/graphql")
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
    }

    private
    fun toQueryJson(query: String) = objectMapper.writeValueAsString(mapOf("query" to query))
}

