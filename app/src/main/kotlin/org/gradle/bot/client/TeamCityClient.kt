package org.gradle.bot.client

import com.google.inject.ImplementedBy
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import java.util.TreeSet
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import org.gradle.bot.model.BuildConfiguration
import org.gradle.bot.model.BuildStage
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildId
import org.jetbrains.teamcity.rest.BuildState
import org.jetbrains.teamcity.rest.TeamCityInstanceFactory
import org.slf4j.LoggerFactory

@ImplementedBy(DefaultTeamCityClient::class)
interface TeamCityClient {
    fun triggerBuild(stage: BuildStage, branchName: String): Future<Build>

    fun findBuild(teamCityBuildId: String): Future<Build?>

    fun getLatestFinishedBuild(targetBranch: String): Future<Build>

    /**
     * This method may fail when cancelling a finished build
     */
    fun cancelBuild(build: Build, reason: String): Future<Unit>

    fun searchFlakyBuildInDependencies(build: Build, predicate: (Build) -> Boolean): Future<Build?>

    fun getAllDependencies(build: Build): Future<Set<Build>>

    fun isFirstFlakyBuild(build: Build, predicate: (Build) -> Boolean): Future<Boolean>
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
        buildConfiguration.runBuild(logicalBranchName = branchName)
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

    override fun searchFlakyBuildInDependencies(build: Build, predicate: (Build) -> Boolean): Future<Build?> = executeBlocking {
        doSearchFlakyBuildInDependencies(build, predicate)
    }

    private fun doSearchFlakyBuildInDependencies(build: Build, predicate: (Build) -> Boolean): Build? {
        build.snapshotDependencies.forEach {
            if (predicate(it)) {
                return it
            }
            if (it.hasDependencies()) {
                val result = doSearchFlakyBuildInDependencies(it, predicate)
                if (result != null) {
                    return result
                }
            }
        }
        return null
    }

    /**
     * Including the build itself
     */
    override fun getAllDependencies(build: Build): Future<Set<Build>> = executeBlocking {
        val ret: TreeSet<Build> = TreeSet { x, y -> x.id.stringId.compareTo(y.id.stringId) }
        putDependenciesInto(build, ret)
        ret
    }

    override fun isFirstFlakyBuild(build: Build, predicate: (Build) -> Boolean): Future<Boolean> = executeBlocking {
        val last5Builds = teamCityRestClient.builds()
            .fromConfiguration(build.buildConfigurationId)
            .withBranch(build.branch.name!!)
            .includeFailed()
            .includeCanceled()
            .limitResults(5)
            .all()
            .toList()
            .sortedByDescending { it.startDateTime }
        isFirstTime(build, last5Builds, predicate)
    }

    private fun isFirstTime(build: Build, builds: List<Build>, predicate: (Build) -> Boolean): Boolean {
        logger.debug("Current build {}, last 5 builds: {}", build.id.stringId, builds.map { it.id.stringId })
        val index = builds.indexOfFirst { it.id == build.id }
        return when {
            builds.size == 1 -> true
            index == -1 -> false
            else -> !predicate(builds[index + 1])
        }
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
