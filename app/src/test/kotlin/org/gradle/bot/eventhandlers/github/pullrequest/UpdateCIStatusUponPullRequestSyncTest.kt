package org.gradle.bot.eventhandlers.github.pullrequest

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.vertx.core.Future
import org.gradle.bot.fixtures.AbstractMockKTest
import org.gradle.bot.model.CommitStatusState
import org.gradle.bot.model.PullRequestGitHubEvent
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class UpdateCIStatusUponPullRequestSyncTest : AbstractMockKTest() {
    @MockK(relaxed = true)
    lateinit var event: PullRequestGitHubEvent
    @MockK(relaxed = true)
    lateinit var build: Build

    lateinit var handler: UpdateCIStatusUponPullRequestSync

    @BeforeEach
    fun setUp() {
        handler = UpdateCIStatusUponPullRequestSync(gitHubClient, teamCityClient)
    }

    @ParameterizedTest(name = "Skip pull_request events with action {0}")
    @CsvSource(value = [
        "assigned",
        "unassigned",
        "labeled",
        "unlabeled",
        "edited",
        "closed",
        "ready_for_review",
        "locked",
        "unlocked"
    ])
    fun `skip event if action is not open reopen sync`(action: String) {
        // given
        every { event.action } returns action

        // when
        handler.handleEvent(event)

        // then
        verifyGitHubTeamCityClientsNotCalled()
    }

    @ParameterizedTest(name = "Skip forked PRs with action {0}")
    @CsvSource(value = [
        "opened",
        "synchronize",
        "reopened"
    ])
    fun `skip forked PR events`(action: String) {
        // given
        every { event.action } returns action
        every { event.pullRequest.head.repo.fork } returns true

        // when
        handler.handleEvent(event)

        // then
        verifyGitHubTeamCityClientsNotCalled()
    }

    @Test
    fun `skip events targeting other branch`() {
        // given
        every { event.action } returns "reopened"
        every { event.getTargetBranch() } returns "other-branch"

        // when
        handler.handleEvent(event)

        // then
        verifyGitHubTeamCityClientsNotCalled()
    }

    @Test
    fun `publish latest build status to PR`() {
        // given
        every { event.action } returns "opened"
        every { event.getTargetBranch() } returns "master"
        every { teamCityClient.getLatestFinishedBuild("master") } returns Future.succeededFuture(build)
        every { build.status } returns BuildStatus.FAILURE
        every { event.getRepoFullName() } returns "gradle/gradle"
        every { event.getHeadSha() } returns "1a2b3c"
        every { build.branch.name } returns "master"
        every { build.getHomeUrl() } returns "https://buildUrl"

        // when
        handler.handleEvent(event)

        // then
        verify {
            gitHubClient.createCommitStatus(
                "gradle/gradle",
                "1a2b3c",
                CommitStatusState.FAILURE,
                "https://buildUrl",
                withArg { assert(it.startsWith("master branch failure since ")) },
                ciStatusContext
            )
        }
    }
}
