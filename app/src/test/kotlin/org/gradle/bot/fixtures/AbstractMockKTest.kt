package org.gradle.bot.fixtures

import io.mockk.Called
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
open class AbstractMockKTest {
    @MockK(relaxed = true)
    lateinit var gitHubClient: GitHubClient

    @MockK(relaxed = true)
    lateinit var teamCityClient: TeamCityClient

    fun verifyGitHubTeamCityClientsNotCalled() {
        verify {
            gitHubClient wasNot Called
            teamCityClient wasNot Called
        }
    }
}
