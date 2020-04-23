package org.gradle.bot.eventhandlers.teamcity

import com.google.inject.ImplementedBy
import javax.inject.Singleton
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildProblemOccurrence
import org.jetbrains.teamcity.rest.BuildStatus
import org.jetbrains.teamcity.rest.TestRun
import org.jetbrains.teamcity.rest.TestStatus
import org.slf4j.LoggerFactory

/**
 * Determines if a build is flaky. Usually blocking because it needs to access build.problems/build.testRuns() blocking API
 */
@ImplementedBy(FlakyBuildPatternComposite::class)
interface FlakyBuildPattern {
    fun isFlakyBuild(build: CachingBuild): Boolean
}

class CachingBuild(private val delegate: Build) : Build by delegate {
    val problems: List<BuildProblemOccurrence> by lazy {
        delegate.buildProblems.toList()
    }
    val failedTests: List<TestRun> by lazy {
        delegate.testRuns(TestStatus.FAILED).toList()
    }

    override val buildProblems
        get() = throw UnsupportedOperationException()

    override fun testRuns(status: TestStatus?) = throw UnsupportedOperationException()

    fun getErrorMessage() = problems.joinToString("\n") { it.details } + failedTests.joinToString("\n") { it.details }
}

@Singleton
class FlakyBuildPatternComposite() : FlakyBuildPattern {
    private val patterns: List<FlakyBuildPattern> = listOf(
        BuildTimeoutPattern(),
        Ec2AgentShutDownPattern(),
        ErrorWhenApplyingPatchPattern(),
        ExistingConnectionForciblyClosedPattern()
    )
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun isFlakyBuild(build: CachingBuild): Boolean {
        if (build.status == BuildStatus.SUCCESS) {
            return false
        }
        if (build.failedTests.size > 10) {
            return false
        }

        logger.debug("problems of {}:\n{}", build.getErrorMessage())
        return patterns.any { pattern ->
            pattern.isFlakyBuild(build).also {
                if (it) {
                    logger.debug("Build {} matches flaky pattern {}", build.id, pattern.javaClass.simpleName)
                }
            }
        }
    }
}

// https://github.com/gradle/gradle-private/issues/2956
class ExistingConnectionForciblyClosedPattern : FlakyBuildPattern {
    override fun isFlakyBuild(build: CachingBuild) = build.getErrorMessage().contains("An existing connection was forcibly closed by the remote host")
}

// https://github.com/gradle/gradle-private/issues/2174
class ErrorWhenApplyingPatchPattern : FlakyBuildPattern {
    override fun isFlakyBuild(build: CachingBuild): Boolean = build.statusText?.contains("Error while applying patch") ?: false
}

// https://github.com/gradle/gradle-private/issues/3030
// Execution timeout
// Process exited with code 143 (Step: GRADLE_RUNNER (Gradle))
class BuildTimeoutPattern : FlakyBuildPattern {
    override fun isFlakyBuild(build: CachingBuild): Boolean = build.getErrorMessage().contains("Execution timeout")
}

// Process exited with code 143 (Step: GRADLE_RUNNER (Gradle))
class Ec2AgentShutDownPattern : FlakyBuildPattern {
    override fun isFlakyBuild(build: CachingBuild): Boolean {
        // This is not 100% accurate
        // We can't get "Canceled with comment: Build agent shutdown"
        // unless we download the whole build log file
        // so this is an approximation
        // If error is "Canceled exit code 143" and no cancel user and agent == null (because the ec2 agent is already shutdown)
        return build.statusText?.contains("Canceled") ?: false &&
            build.statusText?.contains("exit code 143") ?: false &&
            build.agent == null &&
            build.canceledInfo?.user == null
    }
}

class DiskOutOfSpacePattern : FlakyBuildPattern {
    override fun isFlakyBuild(build: CachingBuild): Boolean {
        TODO("Not yet implemented")
    }
}
