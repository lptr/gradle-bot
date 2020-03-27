package org.gradle.bot

import org.gradle.bot.model.BuildStage
import org.jetbrains.teamcity.rest.BuildState

interface PullRequestCommand {
    fun execute(context: PullRequestContext)

    val sourceComment: PullRequestComment
}

class TestCommand(private val targetStage: BuildStage, override val sourceComment: PullRequestComment) : PullRequestCommand {
    override fun execute(context: PullRequestContext) {
        context.findLatestTriggeredBuild().onSuccess {
            if (it == null) {
                triggerBuild(context)
            } else if (it.state == BuildState.QUEUED || it.state == BuildState.RUNNING) {
                context.reply(sourceComment,
                        "Sorry [the build you've already triggered](${it.getHomeUrl()}) is still running, I will not trigger a new one."
                )
            } else {
                triggerBuild(context)
            }
        }.onFailure {
            context.reply(sourceComment, "Sorry some internal error occurs, please contact the administrator @blindpirate")
        }
    }

    private fun triggerBuild(context: PullRequestContext) {
        context.triggerBuild(targetStage).onSuccess {
            context.publishPendingStatuses(targetStage.dependencies)
            context.reply(sourceComment,
                    "OK, I've already triggered [${targetStage.fullName} build](${it.getHomeUrl()}) for you.",
                    it.id.stringId)
        }.onFailure {
            context.reply(sourceComment, "Sorry some internal error occurs, please contact the administrator @blindpirate")
        }
    }
}

class UnknownCommand(override val sourceComment: PullRequestComment) : PullRequestCommand {
    override fun execute(context: PullRequestContext) {
        context.reply(sourceComment, context.iDontUnderstandWhatYouSaid())
    }
}

class TestAndMergeCommand

class StopCommand


class HelpCommand(override val sourceComment: PullRequestComment) : PullRequestCommand {
    override fun execute(context: PullRequestContext) {
        context.reply(sourceComment, context.helpMessage())
    }
}

class NoOpCommand(override val sourceComment: PullRequestComment) : PullRequestCommand {
    override fun execute(context: PullRequestContext) {}
}
