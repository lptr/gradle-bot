package org.gradle.bot.client

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import org.gradle.bot.model.BuildStage
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildId
import org.jetbrains.teamcity.rest.TeamCityInstanceFactory
import org.slf4j.LoggerFactory

@Singleton
class TeamCityClient @Inject constructor(
    private val vertx: Vertx,
    @Named("TEAMCITY_ACCESS_TOKEN") teamCityToken: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val teamCityRestClient by lazy {
        TeamCityInstanceFactory.tokenAuth("https://builds.gradle.org", teamCityToken)
    }

    private fun <T> executeBlocking(function: () -> T): Future<T> {
        return vertx.executeBlocking { promise: Promise<T> ->
            try {
                promise.complete(function())
            } catch (e: Throwable) {
                logger.error("", e)
                promise.fail(e)
            }
        }
    }

    fun triggerBuild(stage: BuildStage, branchName: String) = executeBlocking {
        val buildConfiguration = teamCityRestClient.buildConfiguration(stage.toBuildConfigurationId())
        val build = buildConfiguration.runBuild(logicalBranchName = branchName)
        build
    }

    fun findBuild(teamCityBuildId: String): Future<Build?> = executeBlocking { teamCityRestClient.build(BuildId(teamCityBuildId)) }

    fun getLatestFinishedBuild(targetBranch: String) = executeBlocking {
        val buildConfigurationId = BuildStage.READY_FOR_NIGHTLY.toBuildConfigurationId()
        val latestFinishedBuild = teamCityRestClient
            .builds()
            .fromConfiguration(buildConfigurationId)
            .withBranch(targetBranch)
            .includeFailed()
            .latest()
        latestFinishedBuild!!
    }
}
