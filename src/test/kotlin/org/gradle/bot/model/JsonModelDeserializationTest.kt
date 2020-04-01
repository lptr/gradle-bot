package org.gradle.bot.model

import java.io.File
import org.gradle.bot.objectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class JsonModelDeserializationTest {
    private val events = listOf(
        CommitStatusGitHubEvent::class.java,
        PullRequestGitHubEvent::class.java,
        PullRequestWithCommentsResponse::class.java
    )

    @TestFactory
    fun `can deserialize model jsons`(): Iterable<DynamicTest> {
        return events.flatMap { findForEventClass(it) }
    }

    private fun findForEventClass(klass: Class<*>): Iterable<DynamicTest> {
        val modelJsonDir = File(javaClass.classLoader.getResource("modeljson/${klass.simpleName}").file)
        Assertions.assertTrue(modelJsonDir.listFiles()?.size != 0)
        return modelJsonDir.listFiles().map { oneTest(it, klass) }
    }

    private fun oneTest(json: File, klass: Class<*>): DynamicTest =
        DynamicTest.dynamicTest(json.nameWithoutExtension) {
            objectMapper.readValue(json.readText(), klass)
        }
}
