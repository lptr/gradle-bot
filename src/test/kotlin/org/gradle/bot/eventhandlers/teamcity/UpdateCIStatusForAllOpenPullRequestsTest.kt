package org.gradle.bot.eventhandlers.teamcity

import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.eventhandlers.github.pullrequest.ciStatusContext
import org.gradle.bot.model.BuildStage
import org.gradle.bot.model.CommitStatusState
import org.jetbrains.teamcity.rest.Build
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UpdateCIStatusForAllOpenPullRequestsTest {
    @MockK
    lateinit var gitHubClient: GitHubClient

    @MockK
    lateinit var teamCityClient: TeamCityClient

    @MockK
    lateinit var build: Build

    lateinit var handler: UpdateCIStatusForAllOpenPullRequests

    @BeforeEach
    fun setUp() {
        handler = UpdateCIStatusForAllOpenPullRequests(gitHubClient, teamCityClient)
    }

    @Test
    fun `publish statuses to prs if status not set`() {
        // given
        val masterReadyForNightlySuccessBuildId = "1111"
        val prHeadCommit = "1a2b3c"
        val buildUrl = "https://buildUrl"
        val event = createTeamCityEvent("success", BuildStage.READY_FOR_NIGHTLY.buildTypeId, masterReadyForNightlySuccessBuildId)
        // when
        handler.handleEvent(event)
        // then
        verify(exactly = 1) {
            gitHubClient.createCommitStatus(
                "gradle/gradle",
                prHeadCommit,
                CommitStatusState.SUCCESS,
                buildUrl,
                withArg { assert(it.startsWith("master branch success since ")) },
                ciStatusContext
            )
        }
    }

    @Test
    fun `publish statuses to prs if status not equal`() {

    }

    @Test
    fun `skip running status`() {
    }

    @Test
    fun `skip non-ReadyForNightly webhook`() {
    }

    @Test
    fun `skip non-master-release-branch`() {
    }

    @Test
    fun `skip pr with same status`() {
    }

    @Test
    fun `skip forked PRs`() {

    }
}


fun createTeamCityEvent(status: String, buildTypeId: String, buildId: String) =
    TeamCityBuildEvent("$status - <https://builds.gradle.org/viewLog.html?buildTypeId=$buildTypeId&buildId=$buildId|#1503>")