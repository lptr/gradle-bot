package org.gradle.bot.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TeamCityBuildConfigurationsTest {
    @ParameterizedTest(name = "{0} has {1} dependencies")
    @CsvSource(value = [
        "COMPILE_ALL, 1",
        "SANITY_CHECK, 2",
        "QUICK_FEEDBACK_LINUX, 3",
        "QUICK_FEEDBACK, 4",
        "READY_FOR_MERGE, 11",
        "READY_FOR_NIGHTLY, 12",
        "READY_FOR_RELEASE, 13"
    ])
    fun getAllBuildConfigurationDependencies(stage: BuildStage, size: Int) {
        Assertions.assertEquals(size, stage.getAllBuildConfigurationDependencies().size)
    }
}
