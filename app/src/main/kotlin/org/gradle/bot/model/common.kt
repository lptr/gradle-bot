package org.gradle.bot.model

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

enum class BuildConfiguration(val id: String, val configName: String) {
    COMPILE_ALL("Gradle_Check_CompileAll", "Compile All (Quick Feedback - Linux Only)"),
    SANITY_CHECK("Gradle_Check_SanityCheck", "Sanity Check (Quick Feedback - Linux Only)"),
    QUICK_FEEDBACK_LINUX("Gradle_Check_Stage_QuickFeedbackLinuxOnly_Trigger", "Quick Feedback - Linux Only (Trigger) (Check)"),
    QUICK_FEEDBACK("Gradle_Check_Stage_QuickFeedback_Trigger", "Quick Feedback (Trigger) (Check)"),
    GRADLECEPTION("Gradle_Check_Gradleception", "Gradleception - Java8 Linux (Ready for Merge)"),
    INSTANT_SMOKE_TEST_JDK14("Gradle_Check_InstantSmokeTestsJava14", "Smoke Tests with 3rd Party Plugins (instantSmokeTest) - Java14 Linux (Ready for Merge)"),
    SMOKE_TEST_JDK8("Gradle_Check_InstantSmokeTestsJava8", "Smoke Tests with 3rd Party Plugins (instantSmokeTest) - Java8 Linux (Ready for Merge)"),
    SMOKE_TEST_JDK14("Gradle_Check_SmokeTestsJava14", "Smoke Tests with 3rd Party Plugins (smokeTest) - Java14 Linux (Ready for Merge)"),
    BUILD_DISTRIBUTIONS("Gradle_Check_BuildDistributions", "Build Distributions (Ready for Merge)"),
    PERFORMANCE_COORDINATOR("Gradle_Check_PerformanceTestCoordinator", "Performance Regression Test Coordinator - Linux (Ready for Merge)"),
    READY_FOR_MERGE("Gradle_Check_Stage_ReadyforMerge_Trigger", "Ready for Merge (Trigger) (Check)"),
    READY_FOR_NIGHTLY("Gradle_Check_Stage_ReadyforNightly_Trigger", "Ready for Nightly (Trigger) (Check)")
}

enum class BuildStage(val fullName: String, val abbr: String, val buildTypeId: String, val dependencies: List<BuildConfiguration>) {
    COMPILE_ALL(
        "CompileAll",
        "CA",
        "Gradle_Check_CompileAll",
        listOf(BuildConfiguration.COMPILE_ALL)
    ),
    SANITY_CHECK(
        "SanityCheck",
        "SC",
        "Gradle_Check_SanityCheck",
        COMPILE_ALL.dependencies.toMutableList().also { it.add(BuildConfiguration.SANITY_CHECK) }
    ),
    QUICK_FEEDBACK_LINUX(
        "QuickFeedbackLinux",
        "QFL",
        "Gradle_Check_Stage_QuickFeedbackLinuxOnly_Trigger",
        SANITY_CHECK.dependencies.toMutableList().also { it.add(BuildConfiguration.QUICK_FEEDBACK_LINUX) }
    ),
    QUICK_FEEDBACK(
        "QuickFeedback",
        "QF",
        "Gradle_Check_Stage_QuickFeedback_Trigger",
        QUICK_FEEDBACK_LINUX.dependencies.toMutableList().also { it.add(BuildConfiguration.QUICK_FEEDBACK) }
    ),
    READY_FOR_MERGE(
        "ReadyForMerge",
        "RFM",
        "Gradle_Check_Stage_ReadyforMerge_Trigger",
        QUICK_FEEDBACK.dependencies.toMutableList().also {
            it.addAll(listOf(BuildConfiguration.GRADLECEPTION, BuildConfiguration.INSTANT_SMOKE_TEST_JDK14, BuildConfiguration.SMOKE_TEST_JDK8,
                BuildConfiguration.SMOKE_TEST_JDK14, BuildConfiguration.BUILD_DISTRIBUTIONS, BuildConfiguration.PERFORMANCE_COORDINATOR,
                BuildConfiguration.READY_FOR_MERGE)
            )
        }
    ),
    READY_FOR_NIGHTLY(
        "ReadyForNightly",
        "RFN",
        "Gradle_Check_Stage_ReadyforNightly_Trigger",
        BuildConfiguration.values().toList()
    ),
    READY_FOR_RELEASE(
        "ReadyForRelease",
        "RFR",
        "Gradle_Check_Stage_ReadyforRelease_Trigger",
        BuildConfiguration.values().toList()
    );

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
