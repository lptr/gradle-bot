package org.gradle.bot.eventhandlers.teamcity

import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.vertx.core.Future
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_INSTANT
import java.time.temporal.ChronoUnit
import org.gradle.bot.eventhandlers.github.pullrequest.ciStatusContext
import org.gradle.bot.fixtures.AbstractMockKTest
import org.gradle.bot.model.BuildStage
import org.gradle.bot.model.CommitStatusState
import org.gradle.bot.model.ListOpenPullRequestsResponse
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateCIStatusForAllOpenPullRequestsTest : AbstractMockKTest() {
    @RelaxedMockK
    lateinit var build: Build

    @RelaxedMockK
    lateinit var listOpenPullRequestsResponse: ListOpenPullRequestsResponse

    @RelaxedMockK
    lateinit var prNode: ListOpenPullRequestsResponse.Node

    lateinit var handler: UpdateCIStatusForAllOpenPullRequests

    val commitContexts = mutableListOf<ListOpenPullRequestsResponse.Context>()

    val buildUrl = "https://buildUrl"

    val prHeadCommit = "1a2b3c"

    @BeforeEach
    fun setUp() {
        handler = UpdateCIStatusForAllOpenPullRequests(gitHubClient, teamCityClient)

        every { teamCityClient.findBuild(any()) } returns Future.succeededFuture(build)
        every { build.getHomeUrl() } returns buildUrl
        every { gitHubClient.listOpenPullRequests("gradle/gradle") } returns Future.succeededFuture(listOpenPullRequestsResponse)
        every { listOpenPullRequestsResponse.data.repository.pullRequests.nodes } returns listOf(prNode)
        every { prNode.commits.nodes.get(0).commit.status.contexts } returns commitContexts
        every { prNode.headRef.target.oid } returns prHeadCommit
        every { prNode.commits.nodes[0].commit.committedDate } returns ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).format(ISO_INSTANT)
    }

    @Test
    fun `publish statuses to prs if status not set`() {
        // given
        val event = createTeamCityEvent("success", BuildStage.READY_FOR_NIGHTLY.buildTypeId, "1111")
        commitContexts.add(
            ListOpenPullRequestsResponse.Context("failure", "url1", "desc1", "otherContext")
        )

        every { build.branch.name } returns "master"
        every { build.status } returns BuildStatus.SUCCESS

        // when
        handler.handleEvent(event)

        // then
        verify(exactly = 1) {
            gitHubClient.createCommitStatus(
                "gradle/gradle",
                prHeadCommit,
                CommitStatusState.SUCCESS,
                buildUrl,
                withArg {
                    println(it)
                    assert(it.startsWith("master branch success since "))
                },
                ciStatusContext
            )
        }
    }

    @Test
    fun `publish statuses to prs if status not equal`() {
        // given
        val event = createTeamCityEvent("success", BuildStage.READY_FOR_NIGHTLY.buildTypeId, "1111")
        commitContexts.add(
            ListOpenPullRequestsResponse.Context("failure", "url1", "desc1", ciStatusContext)
        )
        every { build.branch.name } returns "master"
        every { build.status } returns BuildStatus.SUCCESS

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
    fun `skip running status`() {
        // given
        val event = createTeamCityEvent("running", BuildStage.READY_FOR_NIGHTLY.buildTypeId, "2222")

        // when
        handler.handleEvent(event)

        // then
        verifyGitHubTeamCityClientsNotCalled()
    }

    @Test
    fun `skip non-ReadyForNightly webhook`() {
        // given
        val masterBranchReadyForReleaseSuccessBuildId = "3333"
        val event = createTeamCityEvent("success", BuildStage.READY_FOR_MERGE.buildTypeId, masterBranchReadyForReleaseSuccessBuildId)

        // when
        handler.handleEvent(event)

        // then
        verifyGitHubTeamCityClientsNotCalled()
    }

    @Test
    fun `skip non-master-release-branch`() {
        // given
        val otherBranchReadyForNightlySuccessBuildId = "4444"
        val event = createTeamCityEvent("success", BuildStage.READY_FOR_NIGHTLY.buildTypeId, otherBranchReadyForNightlySuccessBuildId)
        every { build.branch.name } returns "other-branch"

        // when
        handler.handleEvent(event)

        // then
        verify {
            gitHubClient wasNot Called
        }
    }

    @Test
    fun `skip pr with same status`() {
        // given
        val event = createTeamCityEvent("success", BuildStage.READY_FOR_NIGHTLY.buildTypeId, "1111")
        commitContexts.add(
            ListOpenPullRequestsResponse.Context("success", "url1", "desc1", ciStatusContext)
        )
        every { build.branch.name } returns "master"
        every { build.status } returns BuildStatus.SUCCESS

        // when
        handler.handleEvent(event)

        // then
        verify(exactly = 0) {
            gitHubClient.createCommitStatus(any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `skip stale pr`() {
        // given
        val event = createTeamCityEvent("success", BuildStage.READY_FOR_NIGHTLY.buildTypeId, "1111")
        commitContexts.add(
            ListOpenPullRequestsResponse.Context("failure", "url1", "desc1", ciStatusContext)
        )
        every { build.branch.name } returns "master"
        every { build.status } returns BuildStatus.SUCCESS
        every { prNode.commits.nodes[0].commit.committedDate } returns ZonedDateTime.ofInstant(Instant.now().minus(31, ChronoUnit.DAYS), ZoneId.systemDefault()).format(ISO_INSTANT)

        // when
        handler.handleEvent(event)

        // then
        verify(exactly = 0) {
            gitHubClient.createCommitStatus(any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `skip forked PRs`() {
        val event = createTeamCityEvent("success", BuildStage.READY_FOR_NIGHTLY.buildTypeId, "1111")
        commitContexts.add(
            ListOpenPullRequestsResponse.Context("success", "url1", "desc1", ciStatusContext)
        )

        every { build.branch.name } returns "master"
        every { build.status } returns BuildStatus.SUCCESS

        // when
        handler.handleEvent(event)

        // then
        verify(exactly = 0) {
            gitHubClient.createCommitStatus(any(), any(), any(), any(), any(), any())
        }
    }
}

fun createTeamCityEvent(status: String, buildTypeId: String, buildId: String) =
    TeamCityBuildEvent("$status - <https://builds.gradle.org/viewLog.html?buildTypeId=$buildTypeId&buildId=$buildId|#1503>")
