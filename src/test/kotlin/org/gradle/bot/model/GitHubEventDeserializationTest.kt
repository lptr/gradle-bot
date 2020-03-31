package org.gradle.bot.model

import java.io.File
import org.gradle.bot.objectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class GitHubEventDeserializationTest {
    private val events = listOf(
            CommitStatusGitHubEvent::class.java,
            PullRequestGitHubEvent::class.java
    )

    @TestFactory
    fun `can deserialize GitHubEvents`(): Iterable<DynamicTest> {
        return events.flatMap { findForEventClass(it) }
    }

    private fun findForEventClass(klass: Class<out GitHubEvent>): Iterable<DynamicTest> {
        val eventJsonDir = File(javaClass.classLoader.getResource("eventjson/${klass.simpleName}").file)
        Assertions.assertTrue(eventJsonDir.listFiles()?.size != 0)
        return eventJsonDir.listFiles().map { oneTest(it, klass) }
    }

    private fun oneTest(json: File, klass: Class<out GitHubEvent>): DynamicTest =
            DynamicTest.dynamicTest(json.nameWithoutExtension) {
                objectMapper.readValue(json.readText(), klass)
            }
}
