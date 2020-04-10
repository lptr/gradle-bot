package org.gradle.bot.eventhandlers.github.issuecomment

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.vertx.core.Future
import org.gradle.bot.model.BuildStage
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class TestCommandTest {
    @MockK
    lateinit var comment: PullRequestComment

    @MockK(relaxed = true)
    lateinit var context: PullRequestContext

    @MockK
    lateinit var build: Build

    lateinit var testCommand: TestCommand

    @BeforeEach
    fun setUp() {
        testCommand = TestCommand(BuildStage.READY_FOR_MERGE, comment)
        every { build.getHomeUrl() } returns "http://buildUrl"
        every { build.id.stringId } returns "buildId"
    }

    @Test
    fun `reply contact administrator upon errors`() {
        // given
        every { context.findLatestTriggeredBuild() }.returns(Future.failedFuture("OOPS!"))

        // when
        testCommand.execute(context)

        // then
        verify { context.reply(comment, contactAdminComment) }
    }

    @Test
    fun `trigger new build if no history found`() {
        // given
        every { context.findLatestTriggeredBuild() }.returns(Future.succeededFuture(null))
        every { context.triggerBuild(BuildStage.READY_FOR_MERGE) }.returns(Future.succeededFuture(build))

        // when
        testCommand.execute(context)

        // then
        verify { context.triggerBuild(BuildStage.READY_FOR_MERGE) }
        verify { context.updatePendingStatuses(BuildStage.READY_FOR_MERGE) }
        verify {
            context.reply(comment,
                "OK, I've already triggered [ReadyForMerge build](http://buildUrl) for you.",
                "buildId")
        }
    }

    @Test
    fun `does not trigger new build if current build is running`() {
        // given
        every { context.findLatestTriggeredBuild() }.returns(Future.succeededFuture(build))
        every { build.state }.returns(BuildState.RUNNING)

        // when
        testCommand.execute(context)

        // then
        verify {
            context.reply(comment, "Sorry [the build you've already triggered](http://buildUrl) is still running, I will not trigger a new one.")
        }
        verify(exactly = 0) {
            context.triggerBuild(any())
        }
    }

    @Test
    fun `trigger new build if previous build is finished`() {
        // given
        every { context.findLatestTriggeredBuild() }.returns(Future.succeededFuture(build))
        every { build.state }.returns(BuildState.FINISHED)
        every { context.triggerBuild(BuildStage.READY_FOR_MERGE) }.returns(Future.succeededFuture(build))

        // when
        testCommand.execute(context)

        // then
        verify { context.triggerBuild(BuildStage.READY_FOR_MERGE) }
        verify { context.updatePendingStatuses(BuildStage.READY_FOR_MERGE) }
        verify {
            context.reply(comment,
                "OK, I've already triggered [ReadyForMerge build](http://buildUrl) for you.",
                "buildId")
        }
    }
}
