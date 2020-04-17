package org.gradle.bot.eventhandlers.github.issuecomment

import io.mockk.mockk
import org.gradle.bot.eventhandlers.github.TestCommand
import org.gradle.bot.eventhandlers.github.pullrequest.CommandComment
import org.gradle.bot.model.BuildStage
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CommandCommentTest {
    @ParameterizedTest(name = "can parse {0}")
    @ValueSource(strings = [
        "@bot-gradle test this",
        "@bot-gradle test this,please",
        "@bot-gradle test this plz",
        "@bot-gradle test RFM plz",
        "@bot-gradle test ReadyForMerge",
        "@bot-gradle test readyformerge"
    ])
    fun parseCommandInCommentTest(commentBody: String) {
        val commandComment = CommandComment(1L, commentBody, mockk(relaxed = true), mockk(relaxed = true), true).command
        assert(commandComment is TestCommand && commandComment.targetStage == BuildStage.READY_FOR_MERGE)
    }
}
