package org.gradle.bot.client

import com.google.inject.ImplementedBy
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.gradle.bot.model.BuildConfiguration
import org.gradle.bot.model.BuildStage
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildId
import org.jetbrains.teamcity.rest.BuildState
import org.jetbrains.teamcity.rest.TeamCityInstanceFactory
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

fun main() {
    val client = DefaultTeamCityClient(Vertx.vertx(), System.getenv("TEAMCITY_ACCESS_TOKEN"))

    client.findBuild("33611492").onSuccess {
        client.getAllDependencies(it!!).onSuccess {
            println(it.size)
        }
    }
}
//    = snapshotDependencies.toMutableList().also { it.add(this) }

@ImplementedBy(DefaultTeamCityClient::class)
interface TeamCityClient {
    fun triggerBuild(stage: BuildStage, branchName: String): Future<Build>

    fun findBuild(teamCityBuildId: String): Future<Build?>

    fun getLatestFinishedBuild(targetBranch: String): Future<Build>

    /**
     * This method may fail when cancelling a finished build
     */
    fun cancelBuild(build: Build, reason: String): Future<Unit>

    fun getAllDependencies(build: Build): Future<Set<Build>>
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
        if (build.state == BuildState.FINISHED) {
            logger.debug("Skip cancelling finished build {}", build.getHomeUrl())
        } else {
            logger.debug("Cancelling build {}", build.getHomeUrl())
            build.cancel(reason)
        }
    }

    /**
     * Including the build itself
     */
    override fun getAllDependencies(build: Build): Future<Set<Build>> = executeBlocking {
        val ret: TreeSet<Build> = TreeSet { x, y -> x.id.stringId.compareTo(y.id.stringId) }
        putDependenciesInto(build, ret)
        ret
    }

    private fun Build.hasDependencies() = BuildConfiguration.values().any { it.id == buildConfigurationId.stringId }

    private fun putDependenciesInto(build: Build, result: TreeSet<Build>) {
        result.add(build)
        // snapshotDependencies only contains direct dependencies, no transitive ones
        build.snapshotDependencies
            .filter { !result.contains(it) }
            .forEach {
                if (it.hasDependencies()) {
                    putDependenciesInto(it, result)
                } else {
                    result.add(it)
                }
            }
    }

}
