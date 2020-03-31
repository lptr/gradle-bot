package org.gradle.bot.eventhandlers.teamcity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TeamCityEventTest {
    @ParameterizedTest(name = "can parse {0} event")
    @CsvSource(value = [
        "SUCCESS, Gradle_Check_Quick_2_bucket30,              33115638, success - *Test Coverage - Quick Java8 Oracle Windows (pluginUse_2)* <https://builds.gradle.org/viewLog.html?buildTypeId=Gradle_Check_Quick_2_bucket30&buildId=33115638|#1503> (triggered by Snapshot dependency; Jendrik Johannes; Gradle / Check / Ready for Merge (Trigger))",
        "RUNNING, Gradle_Check_Stage_ReadyforRelease_Trigger, 33105561, running - *Ready for Release (Trigger)* <https://builds.gradle.org/viewLog.html?buildTypeId=Gradle_Check_Stage_ReadyforRelease_Trigger&buildId=33105561|#2651> (triggered by Schedule Trigger)"
    ])
    fun `can extract information from teamcity webhook`(buildStatus: String, buildTypeId: String, buildId: String, text: String) {
        val event = TeamCityBuildEvent(text)
        Assertions.assertEquals(BuildEventStatus.valueOf(buildStatus), event.buildStatus)
        Assertions.assertEquals(buildTypeId, event.buildTypeId)
        Assertions.assertEquals(buildId, event.buildId)
    }
}
