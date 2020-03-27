package org.gradle.bot.client

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.gradle.bot.model.BuildStage
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildConfigurationId
import org.jetbrains.teamcity.rest.BuildId
import org.jetbrains.teamcity.rest.TeamCityInstanceFactory
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamCityClient @Inject constructor(private val vertx: Vertx) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val teamCityRestClient by lazy {
        val token = System.getenv("TEAMCITY_ACCESS_TOKEN") ?: throw IllegalStateException("Must set env variable TEAMCITY_ACCESS_TOKEN")
        TeamCityInstanceFactory.tokenAuth("https://builds.gradle.org", token)
    }

    fun triggerBuild(stage: BuildStage, branchName: String): Future<Build> {
        return vertx.executeBlocking { promise: Promise<Build> ->
            try {
                val buildConfiguration = teamCityRestClient.buildConfiguration(BuildConfigurationId(stage.buildTypeId))
                val build = buildConfiguration.runBuild(logicalBranchName = branchName)
                promise.complete(build)
            } catch (e: Throwable) {
                logger.error("Error when triggering $stage on $branchName", e)
                promise.fail(e)
            }
        }
    }

    fun findBuild(teamCityBuildId: String): Future<Build?> {
        return vertx.executeBlocking {
            try {
                it.complete(teamCityRestClient.build(BuildId(teamCityBuildId)))
            } catch (e: Throwable) {
                logger.error("", e)
                it.fail(e)
            }
        }
    }
}