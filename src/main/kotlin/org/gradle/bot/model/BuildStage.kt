package org.gradle.bot.model

enum class BuildStage(val fullName: String, val abbr: String, val buildTypeId: String) {
    COMPILE_ALL("CompileAll", "CA", "Gradle_Check_CompileAll"),
    SANITY_CHECK("SanityCheck", "SC", "Gradle_Check_SanityCheck"),
    QUICK_FEEDBACK_LINUX("QuickFeedbackLinux", "QFL", "Gradle_Check_Stage_QuickFeedbackLinuxOnly_Trigger"),
    QUICK_FEEDBACK("QuickFeedback", "QF", "Gradle_Check_Stage_QuickFeedback_Trigger"),
    READY_FOR_MERGE("ReadyForMerge", "RFM", "Gradle_Check_Stage_ReadyforMerge_Trigger"),
    READY_FOR_NIGHTLY("ReadyForNightly", "RFN", "Gradle_Check_Stage_ReadyforNightly_Trigger"),
    READY_FOR_RELEASE("ReadyForRelease", "RFR", "Gradle_Check_Stage_ReadyforRelease_Trigger");

    companion object {
        fun parseTargetStage(target: String) = values().find { it.fullName.equals(target, true) || it.abbr.equals(target, true) }
    }
}
