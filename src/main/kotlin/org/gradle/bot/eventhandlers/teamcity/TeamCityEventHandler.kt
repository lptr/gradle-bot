package org.gradle.bot.eventhandlers.teamcity

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.eventbus.Message
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.eventhandlers.WebHookEventHandler
import org.gradle.bot.model.ListOpenPullRequestsResponse
import org.gradle.bot.objectMapper
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

interface TeamCityEventHandler : WebHookEventHandler {
    override val eventPrefix: String
        get() = "teamcity"
}

enum class BuildEventStatus {
    SUCCESS,
    FAILURE,
    RUNNING
}

/*
{
"text":" success - *Test Coverage - Quick Java8 Oracle Windows (pluginUse_2)* <https://builds.gradle.org/viewLog.html?buildTypeId=Gradle_Check_Quick_2_bucket30&buildId=33115638|#1503> (triggered by Snapshot dependency; Jendrik Johannes; Gradle / Check / Ready for Merge (Trigger))"
}
 */

val buildEventPattern = "(success|failure|running) - .*buildTypeId=(\\w+)&buildId=(\\d+)\\|.*".toRegex()

class TeamCityBuildEvent(@JsonProperty("text") private val text: String) {
    val buildStatus: BuildEventStatus
    val buildTypeId: String
    val buildId: String

    init {
        val result: MatchResult = buildEventPattern.find(text)!!
        buildStatus = BuildEventStatus.valueOf(result.groupValues[1].toUpperCase())
        buildTypeId = result.groupValues[2]
        buildId = result.groupValues[3]
    }
}

abstract class AbstractTeamCityEventHandler : TeamCityEventHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    override val eventType: String
        get() = "build"

    override fun handle(event: Message<String>?) {
        try {
            handleEvent(objectMapper.readValue(event?.body(), TeamCityBuildEvent::class.java))
        } catch (e: Throwable) {
            logger.error(event?.body(), e);
        }
    }

    abstract fun handleEvent(event: TeamCityBuildEvent)
}

/**
 * Upon Ready for Nightly builds finish, update all open pull request's head commit with the build status
 */
@Singleton
class UpdateCIStatusForAllOpenPullRequests @Inject constructor(
    private val gitHubClient: GitHubClient,
    private val teamCityClient: TeamCityClient
) : AbstractTeamCityEventHandler() {
    override fun handleEvent(event: TeamCityBuildEvent) {
        gitHubClient.listOpenPullRequests("gradle/gradle").onSuccess {
            updateCIStatusFor(it)
        }
    }

    private fun updateCIStatusFor(response: ListOpenPullRequestsResponse) {
        response.data.repository.pullRequests.nodes
    }
}