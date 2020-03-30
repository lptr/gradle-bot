package org.gradle.bot.integration

import org.gradle.bot.GradleBotVerticle
import org.gradle.bot.client.TeamCityClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock

@ExtendWith(VertxGuiceIntegrationTestExtension::class)
@VertxGuiceIntegrationTest(GradleBotVerticle::class)
class WebHookIntegrationTest {
    @Mock
    lateinit var githubClient: TeamCityClient
    @Mock
    lateinit var teamCityClient: TeamCityClient

    @Test
    fun `can process webhook events`() {

    }
}