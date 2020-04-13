package org.gradle.bot.eventhandlers.github

val contactAdminComment = "Sorry some internal error occurs, please contact the administrator @blindpirate"

val noPermissionComment = "Sorry but I'm afraid you're not allowed to do this."

fun iDontUnderstandWhatYouSaid(botName: String) = """
Sorry I don't understand what you said, please type `@${botName} help` to get help.
"""

fun helpMessage(botName: String) = """Currently I support the following commands:
        
- `@${botName} test {BuildStage}` to trigger a build
  - e.g. `@${botName} test SanityCheck plz`
  - `SanityCheck`/`CompileAll`/`QuickFeedbackLinux`/`QuickFeedback`/`ReadyForMerge`/`ReadyForNightly`/`ReadyForRelease` are supported
  - `test this` means `test ReadyForMerge`
  - `SanityCheck` can be abbreviated as `SC`, `ReadyForMerge` as `RFM`, etc.
- `@${botName} help` to display this message
"""

fun readyForMergeComment(prAuthor: String) = """
    @${prAuthor} this PR is ready for merge.
""".trimIndent()