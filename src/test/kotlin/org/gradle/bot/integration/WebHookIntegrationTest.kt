package org.gradle.bot.integration

import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.Message
import io.vertx.core.eventbus.impl.EventBusImpl
import io.vertx.core.eventbus.impl.HandlerHolder
import io.vertx.core.impl.utils.ConcurrentCyclicSequence
import io.vertx.ext.web.client.WebClient
import org.gradle.bot.GradleBotVerticle
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.eventhandlers.github.GitHubEventHandler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import java.util.concurrent.ConcurrentMap
import javax.inject.Inject
import javax.inject.Singleton

val testEventType = "TestEvent"

@Singleton
class TestGitHubEventHandler : GitHubEventHandler {
    override val eventType = "TestEvent"
    val receivedEvents = mutableListOf<String>()
    override fun handle(event: Message<String>?) {
        event?.let { receivedEvents.add(it.body()) }
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
    @Inject
    lateinit var vertx: Vertx

    @Test
    fun `can process webhook events`() {
        webClient.postAbs("http://localhost:8080/github")
                .putHeader("Accept", "application/json")
                .putHeader("Content-Type", "application/json")
                .putHeader("X-GitHub-Event", testEventType)
                .sendBuffer(Buffer.buffer("{}"))
                .await()

        Thread.sleep(1000)
        Assertions.assertEquals(1, testGitHubEventHandler.receivedEvents.size)
    }

    @Test
    fun `all handlers have been registered`() {
        val eventBus = vertx.eventBus() as EventBusImpl

        val field = EventBusImpl::class.java.getDeclaredField("handlerMap")
        field.isAccessible = true
        val handlerMap = field.get(eventBus) as ConcurrentMap<String, ConcurrentCyclicSequence<HandlerHolder<*>>>

        // IssueCommentEventHandler
        // UpdateCIStatusUponPullRequestChange
        // TestGitHubEventHandler
        Assertions.assertEquals(3, handlerMap.values.flatMap { it }.size)
    }
}
