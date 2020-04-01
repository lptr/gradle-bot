package org.gradle.bot.eventhandlers.github.pullrequest

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.gradle.bot.model.PullRequestGitHubEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CommitToPullRequestInfoLocalCacheHandlerTest {
    private val handler = CommitToPullRequestInfoLocalCacheHandler()

    @Test
    fun `can put events into cache`() {
        // when
        handler.handleEvent(createMockEvent("opened", "commit1", "gradle/gradle", 12345, "my-branch"))
        handler.handleEvent(createMockEvent("synchronize", "commit2", "gradle/gradle", 12345, "my-branch"))
        handler.handleEvent(createMockEvent("synchronize", "commit3", "gradle/gradle", 12345, "my-branch"))
        handler.handleEvent(createMockEvent("opened", "commit3", "gradle/gradle", 23456, "my-branch"))

        // then
        Assertions.assertEquals(
            listOf(PullRequestInformation("my-branch", "gradle/gradle", 12345)),
            handler.getPullRequests("commit1").toList()
        )
        Assertions.assertEquals(
            listOf(PullRequestInformation("my-branch", "gradle/gradle", 12345)),
            handler.getPullRequests("commit2").toList()
        )
        Assertions.assertEquals(
            setOf(PullRequestInformation("my-branch", "gradle/gradle", 12345),
                PullRequestInformation("my-branch", "gradle/gradle", 23456)
            ),
            handler.getPullRequests("commit3").toSet()
        )
    }

    @Test
    fun `can remove from cache`() {
        // when
        handler.handleEvent(createMockEvent("opened", "commit1", "gradle/gradle", 12345, "my-branch"))
        handler.handleEvent(createMockEvent("synchronize", "commit2", "gradle/gradle", 12345, "my-branch"))
        handler.handleEvent(createMockEvent("synchronize", "commit3", "gradle/gradle", 12345, "my-branch"))
        handler.handleEvent(createMockEvent("opened", "commit3", "gradle/gradle", 23456, "my-branch"))
        handler.handleEvent(createMockEvent("closed", "commit3", "gradle/gradle", 12345, "my-branch"))

        // then
        assert(handler.getPullRequests("commit1").isEmpty())
        assert(handler.getPullRequests("commit2").isEmpty())
        Assertions.assertEquals(
            setOf(PullRequestInformation("my-branch", "gradle/gradle", 23456)),
            handler.getPullRequests("commit3").toSet()
        )
    }

    @Test
    fun `can close then reopen`() {
        // when
        handler.handleEvent(createMockEvent("opened", "commit1", "gradle/gradle", 12345, "my-branch"))
        handler.handleEvent(createMockEvent("synchronize", "commit2", "gradle/gradle", 12345, "my-branch"))
        handler.handleEvent(createMockEvent("synchronize", "commit3", "gradle/gradle", 12345, "my-branch"))
        handler.handleEvent(createMockEvent("opened", "commit3", "gradle/gradle", 23456, "my-branch"))
        handler.handleEvent(createMockEvent("closed", "commit3", "gradle/gradle", 12345, "my-branch"))
        handler.handleEvent(createMockEvent("reopened", "commit3", "gradle/gradle", 12345, "my-branch"))

        // then
        assert(handler.getPullRequests("commit1").isEmpty())
        assert(handler.getPullRequests("commit2").isEmpty())
        Assertions.assertEquals(
            setOf(PullRequestInformation("my-branch", "gradle/gradle", 12345),
                PullRequestInformation("my-branch", "gradle/gradle", 23456)
            ),
            handler.getPullRequests("commit3").toSet()
        )
    }

    fun createMockEvent(action: String, commitId: String, repo: String, number: Long, branch: String): PullRequestGitHubEvent {
        val ret = mockk<PullRequestGitHubEvent>(relaxed = true)
        every { ret.action } returns action
        every { ret.getRepoFullName() } returns repo
        every { ret.getSrcBranch() } returns branch
        every { ret.number } returns number
        every { ret.getHeadSha() } returns commitId
        return ret
    }
}
