package org.gradle.bot.model

import org.gradle.bot.model.BuildConfiguration.QUICK_FEEDBACK_LINUX_TRIGGER
import org.gradle.bot.model.BuildConfiguration.QUICK_FEEDBACK_TRIGGER
import org.gradle.bot.model.BuildConfiguration.READY_FOR_MERGE_TRIGGER
import org.gradle.bot.model.BuildConfiguration.READY_FOR_NIGHTLY_TRIGGER
import org.gradle.bot.model.BuildConfiguration.READY_FOR_RELEASE_TRIGGER
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildConfigurationId
import org.jetbrains.teamcity.rest.BuildStatus

// https://developer.github.com/v4/enum/commentauthorassociation/
enum class AuthorAssociation(val admin: Boolean) {
    COLLABORATOR(true),
    CONTRIBUTOR(false),
    FIRST_TIMER(false),
    FIRST_TIME_CONTRIBUTOR(false),
    MEMBER(true),
    NONE(false),
    OWNER(true);

    companion object {
        fun isAdmin(rawValue: String) = valueOf(rawValue).admin
    }
}

enum class CommitStatusState {
    PENDING,
    ERROR,
    FAILURE,
    SUCCESS;

    companion object {
        fun fromTeamCityBuildStatus(buildStatus: BuildStatus): CommitStatusState =
            when (buildStatus) {
                BuildStatus.SUCCESS -> SUCCESS
                BuildStatus.ERROR -> ERROR
                BuildStatus.FAILURE -> FAILURE
                BuildStatus.UNKNOWN -> PENDING
            }

        fun of(value: String) = valueOf(value.toUpperCase())
    }
}

enum class BuildConfiguration(val id: String, val configName: String, val directDependencies: List<BuildConfiguration>) {
    COMPILE_ALL("Gradle_Check_CompileAll", "Compile All (Quick Feedback - Linux Only)", emptyList()),
    SANITY_CHECK("Gradle_Check_SanityCheck", "Sanity Check (Quick Feedback - Linux Only)", listOf(COMPILE_ALL)),
    QUICK_FEEDBACK_LINUX_TRIGGER("Gradle_Check_Stage_QuickFeedbackLinuxOnly_Trigger", "Quick Feedback - Linux Only (Trigger) (Check)", listOf(SANITY_CHECK, COMPILE_ALL)),
    QUICK_FEEDBACK_TRIGGER("Gradle_Check_Stage_QuickFeedback_Trigger", "Quick Feedback (Trigger) (Check)", listOf(COMPILE_ALL, QUICK_FEEDBACK_LINUX_TRIGGER)),
    GRADLECEPTION("Gradle_Check_Gradleception", "Gradleception - Java8 Linux (Ready for Merge)", listOf(QUICK_FEEDBACK_LINUX_TRIGGER)),
    INSTANT_SMOKE_TEST_JDK14("Gradle_Check_InstantSmokeTestsJava14", "Smoke Tests with 3rd Party Plugins (instantSmokeTest) - Java14 Linux (Ready for Merge)", listOf(QUICK_FEEDBACK_LINUX_TRIGGER)),
    SMOKE_TEST_JDK8("Gradle_Check_InstantSmokeTestsJava8", "Smoke Tests with 3rd Party Plugins (instantSmokeTest) - Java8 Linux (Ready for Merge)", listOf(QUICK_FEEDBACK_LINUX_TRIGGER)),
    SMOKE_TEST_JDK14("Gradle_Check_SmokeTestsJava14", "Smoke Tests with 3rd Party Plugins (smokeTest) - Java14 Linux (Ready for Merge)", listOf(QUICK_FEEDBACK_LINUX_TRIGGER)),
    BUILD_DISTRIBUTIONS("Gradle_Check_BuildDistributions", "Build Distributions (Ready for Merge)", listOf(QUICK_FEEDBACK_LINUX_TRIGGER)),
    PERFORMANCE_COORDINATOR("Gradle_Check_PerformanceTestCoordinator", "Performance Regression Test Coordinator - Linux (Ready for Merge)", listOf(QUICK_FEEDBACK_LINUX_TRIGGER)),
    READY_FOR_MERGE_TRIGGER("Gradle_Check_Stage_ReadyforMerge_Trigger", "Ready for Merge (Trigger) (Check)",
        listOf(GRADLECEPTION, INSTANT_SMOKE_TEST_JDK14, SMOKE_TEST_JDK8, QUICK_FEEDBACK_TRIGGER,
            SMOKE_TEST_JDK14, BUILD_DISTRIBUTIONS, PERFORMANCE_COORDINATOR)),
    READY_FOR_NIGHTLY_TRIGGER("Gradle_Check_Stage_ReadyforNightly_Trigger", "Ready for Nightly (Trigger) (Check)", listOf(READY_FOR_MERGE_TRIGGER)),
    READY_FOR_RELEASE_TRIGGER("Gradle_Check_Stage_ReadyforRelease_Trigger", "Ready for Release (Trigger) (Check)", listOf(READY_FOR_NIGHTLY_TRIGGER));

    companion object {
        fun containsBuild(build: Build) = values().any { it.id == build.buildConfigurationId.stringId }
    }
}

enum class BuildStage(val fullName: String, val abbr: String, private val buildConfiguration: BuildConfiguration) {
    COMPILE_ALL(
        "CompileAll",
        "CA",
        BuildConfiguration.COMPILE_ALL
    ),
    SANITY_CHECK(
        "SanityCheck",
        "SC",
        BuildConfiguration.SANITY_CHECK
    ),
    QUICK_FEEDBACK_LINUX(
        "QuickFeedbackLinux",
        "QFL",
        QUICK_FEEDBACK_LINUX_TRIGGER
    ),
    QUICK_FEEDBACK(
        "QuickFeedback",
        "QF",
        QUICK_FEEDBACK_TRIGGER
    ),
    READY_FOR_MERGE(
        "ReadyForMerge",
        "RFM",
        READY_FOR_MERGE_TRIGGER
    ),
    READY_FOR_NIGHTLY(
        "ReadyForNightly",
        "RFN",
        READY_FOR_NIGHTLY_TRIGGER
    ),
    READY_FOR_RELEASE(
        "ReadyForRelease",
        "RFR",
        READY_FOR_RELEASE_TRIGGER
    );

    val buildTypeId: String
        get() = buildConfiguration.id

    fun getAllBuildConfigurationDependencies(): Collection<BuildConfiguration> {
        return getDependencies(this.buildConfiguration)
    }

    private fun getDependencies(buildConfiguration: BuildConfiguration): Set<BuildConfiguration> {
        val ret = mutableSetOf(buildConfiguration)
        buildConfiguration.directDependencies.forEach {
            ret.addAll(getDependencies(it))
        }
        return ret
    }

    companion object {
        fun parseTargetStage(target: String) =
            if (target == "this") {
                READY_FOR_MERGE
            } else {
                values().find { it.fullName.equals(target, true) || it.abbr.equals(target, true) }
            }
    }

    fun toBuildConfigurationId() = BuildConfigurationId(buildTypeId)
}
