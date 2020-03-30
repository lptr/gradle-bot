package org.gradle.bot.integration

import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.WebClient
import org.gradle.bot.GradleBotVerticle
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.eventhandlers.github.AbstractGitHubEventHandler
import org.gradle.bot.model.CommitStatusGitHubEvent
import org.gradle.bot.model.GitHubEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TestGitHubEventHandler : AbstractGitHubEventHandler<CommitStatusGitHubEvent>() {
    val receivedEvents = mutableListOf<GitHubEvent>()
    override fun handleEvent(event: CommitStatusGitHubEvent) {
        receivedEvents.add(event)
    }
}

@ExtendWith(VertxGuiceIntegrationTestExtension::class)
@VertxGuiceIntegrationTest(GradleBotVerticle::class)
class WebHookIntegrationTest {
    @Mock
    lateinit var githubClient: TeamCityClient
    @Mock
    lateinit var teamCityClient: TeamCityClient
    @Inject
    lateinit var webClient: WebClient
    @Inject
    lateinit var testGitHubEventHandler: TestGitHubEventHandler

    @Test
    fun `can process webhook events`() {
        var throwable: Throwable? = null
        val countDownLatch = CountDownLatch(1)
        webClient.postAbs("http://localhost:8080/github")
                .putHeader("Accept", "application/json")
                .putHeader("Content-Type", "application/json")
                .putHeader("X-GitHub-Event", "status")
                .sendBuffer(
                        Buffer.buffer("{}")
                ).onFailure {
                    throwable = it
                }.onComplete {
                    countDownLatch.countDown()
                }

        countDownLatch.await()
        throwable?.also { throw it }

        Thread.sleep(1000)
        Assertions.assertEquals(1, testGitHubEventHandler.receivedEvents.size)
    }
}