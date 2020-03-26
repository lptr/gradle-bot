package org.gradle.bot.client

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.gradle.bot.model.BuildStage
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildConfigurationId
import org.jetbrains.teamcity.rest.TeamCityInstanceFactory
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamCityClient @Inject constructor(private val vertx: Vertx) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val teamCityRestClient by lazy {
        val token = System.getenv("TEAMCITY_ACCESS_TOKEN") ?: throw IllegalStateException("Must set env variable GITHUB_ACCESS_TOKEN")
        TeamCityInstanceFactory.tokenAuth(
                "https://builds.gradle.org",
                token)
    }

    fun triggerBuild(stage: BuildStage, branchName: String): Future<Build> {
        return vertx.executeBlocking { promise: Promise<Build> ->
            val buildConfiguration = teamCityRestClient.buildConfiguration(BuildConfigurationId("Gradle_Check_CompileAll"))
            try {
                promise.complete(buildConfiguration.runBuild(logicalBranchName = branchName))
            } catch (e: Throwable) {
                logger.error("Error when triggering $stage on $branchName", e)
                promise.fail(e)
            }
        }
    }
}