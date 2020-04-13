package org.gradle.bot.client

import com.google.inject.ImplementedBy
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.gradle.bot.model.BuildStage
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildId
import org.jetbrains.teamcity.rest.TeamCityInstanceFactory
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton


/**
 * Including the build itself
 */
fun Build.getAllDependencies() = snapshotDependencies.toMutableList().also { it.add(this) }

@ImplementedBy(DefaultTeamCityClient::class)
interface TeamCityClient {
    fun triggerBuild(stage: BuildStage, branchName: String): Future<Build>

    fun findBuild(teamCityBuildId: String): Future<Build?>

    fun getLatestFinishedBuild(targetBranch: String): Future<Build>

    fun cancelBuild(build: Build, reason: String): Future<Unit>
}

@Singleton
class DefaultTeamCityClient @Inject constructor(
    private val vertx: Vertx,
    @Named("TEAMCITY_ACCESS_TOKEN") teamCityToken: String
) : TeamCityClient {
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

    override fun triggerBuild(stage: BuildStage, branchName: String) = executeBlocking {
        val buildConfiguration = teamCityRestClient.buildConfiguration(stage.toBuildConfigurationId())
        val build = buildConfiguration.runBuild(logicalBranchName = branchName)
        build
    }

    override fun findBuild(teamCityBuildId: String): Future<Build?> = executeBlocking { teamCityRestClient.build(BuildId(teamCityBuildId)) }

    override fun getLatestFinishedBuild(targetBranch: String) = executeBlocking {
        val buildConfigurationId = BuildStage.READY_FOR_NIGHTLY.toBuildConfigurationId()
        val latestFinishedBuild = teamCityRestClient
            .builds()
            .fromConfiguration(buildConfigurationId)
            .withBranch(targetBranch)
            .includeFailed()
            .latest()
        latestFinishedBuild!!
    }

    override fun cancelBuild(build: Build, reason: String) = executeBlocking {
        logger.debug("Cancelling build {}", build.getHomeUrl())
        build.cancel(reason)
    }
}
