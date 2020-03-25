package org.gradle.bot.client

import org.jetbrains.teamcity.rest.BuildConfigurationId
import org.jetbrains.teamcity.rest.TeamCityInstanceFactory
import javax.inject.Singleton

fun main() {
    val teamCityClient = TeamCityInstanceFactory.tokenAuth(
            "https://builds.gradle.org",
            "")

    val buildConfiguration = teamCityClient.buildConfiguration(BuildConfigurationId("Gradle_Check_CompileAll"))
    buildConfiguration.runBuild(logicalBranchName = "master")
}

@Singleton
class TeamCityClient {
}