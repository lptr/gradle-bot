package org.gradle.bot.model

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

enum class CommitStatus {
    PENDING,
    ERROR,
    FAILURE,
    SUCCESS
}

val allBuildTypes = listOf(
        "Compile All (Quick Feedback - Linux Only)",
        "Sanity Check (Quick Feedback - Linux Only)",
        "Quick Feedback - Linux Only (Trigger) (Check)",
        "Quick Feedback (Trigger) (Check)",
        "Gradleception - Java8 Linux (Ready for Merge)",
        "Smoke Tests with 3rd Party Plugins (instantSmokeTest) - Java14 Linux (Ready for Merge)",
        "Smoke Tests with 3rd Party Plugins (instantSmokeTest) - Java8 Linux (Ready for Merge)",
        "Smoke Tests with 3rd Party Plugins (smokeTest) - Java14 Linux (Ready for Merge)",
        "Build Distributions (Ready for Merge)",
        "Performance Regression Test Coordinator - Linux (Ready for Merge)",
        "Ready for Merge (Trigger) (Check)",
        "Ready for Nightly (Trigger) (Check)"
)

enum class BuildStage(val fullName: String, val abbr: String, val buildTypeId: String, val dependencies: List<String>) {
    COMPILE_ALL("CompileAll", "CA", "Gradle_Check_CompileAll", allBuildTypes.subList(0, 1)),
    SANITY_CHECK("SanityCheck", "SC", "Gradle_Check_SanityCheck", allBuildTypes.subList(0, 2)),
    QUICK_FEEDBACK_LINUX("QuickFeedbackLinux", "QFL", "Gradle_Check_Stage_QuickFeedbackLinuxOnly_Trigger", allBuildTypes.subList(0, 3)),
    QUICK_FEEDBACK("QuickFeedback", "QF", "Gradle_Check_Stage_QuickFeedback_Trigger", allBuildTypes.subList(0, 4)),
    READY_FOR_MERGE("ReadyForMerge", "RFM", "Gradle_Check_Stage_ReadyforMerge_Trigger", allBuildTypes.subList(0, 11)),
    READY_FOR_NIGHTLY("ReadyForNightly", "RFN", "Gradle_Check_Stage_ReadyforNightly_Trigger", allBuildTypes),
    READY_FOR_RELEASE("ReadyForRelease", "RFR", "Gradle_Check_Stage_ReadyforRelease_Trigger", allBuildTypes);

    companion object {
        fun parseTargetStage(target: String) = values().find { it.fullName.equals(target, true) || it.abbr.equals(target, true) }
    }
}

