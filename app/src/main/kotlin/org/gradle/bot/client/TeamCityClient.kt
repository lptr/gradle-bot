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

    fun triggerBuild(stage: BuildStage, branchName: String): Future<Build> {
        return vertx.executeBlocking { promise: Promise<Build> ->
            try {
                val buildConfiguration = teamCityRestClient.buildConfiguration(stage.toBuildConfigurationId())
                val build = buildConfiguration.runBuild(logicalBranchName = branchName)
                promise.complete(build)
            } catch (e: Throwable) {
                logger.error("Error when triggering $stage on $branchName", e)
                promise.fail(e)
            }
        }
    }

    fun findBuild(teamCityBuildId: String): Future<Build?> = vertx.executeBlocking {
        try {
            it.complete(teamCityRestClient.build(BuildId(teamCityBuildId)))
        } catch (e: Throwable) {
            logger.error("", e)
            it.fail(e)
        }
    }

fun getLatestFinishedBuild(targetBranch: String): Future<Build> = vertx.executeBlocking {
        try {
            val buildConfigurationId = BuildStage.READY_FOR_NIGHTLY.toBuildConfigurationId()
            val latestFinishedBuild = teamCityRestClient
                    .builds()
                    .fromConfiguration(buildConfigurationId)
                    .withBranch(targetBranch)
                    .includeFailed()
                    .latest()
            it.complete(latestFinishedBuild!!)
        } catch (e: Throwable) {
            logger.error("", e)
            it.fail(e)
        }
    }
}
