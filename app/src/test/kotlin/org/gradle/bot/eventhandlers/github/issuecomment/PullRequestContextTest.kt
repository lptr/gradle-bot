package org.gradle.bot.eventhandlers.github.issuecomment

import io.mockk.every
import io.mockk.excludeRecords
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import io.vertx.core.Future
import org.gradle.bot.fixtures.AbstractMockKTest
import org.gradle.bot.model.BuildStage.READY_FOR_MERGE
import org.gradle.bot.model.CommitStatusState.PENDING
import org.gradle.bot.model.PullRequestWithCommentsResponse
import org.gradle.bot.objectMapper
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PullRequestContextTest : AbstractMockKTest() {
    @MockK
    lateinit var pr: PullRequestWithCommentsResponse
    private val botName = "bot-gradle"

    @BeforeEach
    fun setUp() {
        every { gitHubClient.whoAmI() } returns botName
        every { pr.data.repository.pullRequest.headRef.repository.isFork } returns false
        every { pr.data.repository.pullRequest.headRef.name } returns "release"
        every { pr.data.repository.nameWithOwner } returns "gradle/gradle"
        every { pr.data.repository.pullRequest.headRef.target.oid } returns "headCommit"
        every { pr.data.repository.pullRequest.id } returns "subjectId"
    }

    private fun comment(json: String): PullRequestWithCommentsResponse.Node {
        return objectMapper.readValue(json, PullRequestWithCommentsResponse.Node::class.java)
    }

    @Test
    fun `can parse comments and commands in the comments`() {
        // when
        every { pr.data.repository.pullRequest.commits.nodes } returns emptyList()
        every { pr.data.repository.pullRequest.comments.nodes } returns listOf(
            comment("""
          {
             "databaseId":1,
             "author":{
                "login":"jjohannes"
             },
             "authorAssociation":"MEMBER",
             "body":"@bot-gradle test ReadyForMerge plz"
          }"""),
            comment(""" 
          {
             "databaseId":2,
             "author":{
                "login":"bot-gradle"
             },
             "authorAssociation":"MEMBER",
             "body":"<!-- {\"replyTargetCommentId\":1,\"teamCityBuildId\":\"33016851\",\"teamCityBuildHeadRef\":\"018c8a143d536666e189ad662746f1103b778c02\"} -->\n            \nOK, I've already triggered [ReadyForMerge build](https://builds.gradle.org/viewLog.html?buildId=33016851) for you."
          }"""),
            comment("""
          {
             "databaseId":3,
             "author":{
                "login":"bad-user"
             },
             "authorAssociation":"CONTRIBUTOR",
             "body":"@bot-gradle test ReadyForMerge plz"
          }"""),
            comment("""
          {
             "databaseId":4,
             "author":{
                "login":"bot-gradle"
             },
             "authorAssociation":"MEMBER",
             "body":"[Your build](theUrl) failed."
          }"""),
            comment("""
          {
             "databaseId":5,
             "author":{
                "login":"passerby"
             },
             "authorAssociation":"CONTRIBUTOR",
             "body":"This is an unrelated comment"
          }"""),
            comment("""
          {
             "databaseId":6,
             "author":{
                "login":"collaborator"
             },
             "authorAssociation":"COLLABORATOR",
             "body":"This is an unrelated comment"
          }"""),
            comment("""
          {
             "databaseId":7,
             "author":{
                "login":"jjohannes"
             },
             "authorAssociation":"MEMBER",
             "body":"@bot-gradle test ReadyForMerge plz"
          }  
            """)
        )

        // when
        val context = PullRequestContext(gitHubClient, teamCityClient, pr)

        // then
        assertEquals(
            listOf(
                CommandComment::class,
                BotReplyCommandComment::class,
                CommandComment::class,
                BotNotificationComment::class,
                UnrelatedComment::class,
                UnrelatedComment::class,
                CommandComment::class
            ),
            context.comments.map { it.javaClass.kotlin }
        )

        assertEquals(listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L), context.comments.map { it.id })
        assertTrue((context.comments[0] as CommandComment).isAdmin)
        assertEquals(CommentMetadata(1, "33016851", "018c8a143d536666e189ad662746f1103b778c02"), context.comments[1].metadata)
        context.comments.filterIndexed { index, _ -> index != 1 }.forEach { assertEquals(CommentMetadata(null, null, null), it.metadata) }
        assertFalse((context.comments[2] as CommandComment).isAdmin)
    }

    @Test
    fun `do nothing if the comment is deleted`() {
        // given
        every { pr.data.repository.pullRequest.comments.nodes } returns emptyList()

        // when
        PullRequestContext(gitHubClient, teamCityClient, pr).executeCommentCommand(0)

        // then
        excludeRecords { gitHubClient.whoAmI() }
        verifyGitHubTeamCityClientsNotCalled()
    }

    @Test
    fun `execute commands from admin then reply`() {
        // given
        every { pr.data.repository.pullRequest.commits.nodes } returns emptyList()
        every { pr.data.repository.pullRequest.comments.nodes } returns listOf(
            comment("""
          {
             "databaseId":7,
             "author":{
                "login":"jjohannes"
             },
             "authorAssociation":"MEMBER",
             "body":"@bot-gradle test ReadyForMerge plz"
          }  
            """)
        )
        val build = mockk<Build>()

        every { teamCityClient.triggerBuild(READY_FOR_MERGE, "release") } returns Future.succeededFuture(build)
        every { build.id.stringId } returns "buildId"
        every { build.getHomeUrl() } returns "http://buildUrl"
        val context = PullRequestContext(gitHubClient, teamCityClient, pr)

        // when
        context.executeCommentCommand(7)

        // then
        verify { teamCityClient.triggerBuild(READY_FOR_MERGE, "release") }
        verify {
            gitHubClient.createCommitStatus(
                "gradle/gradle",
                "headCommit",
                withArg<Iterable<String>> {
                    assertEquals(it.toSet(), READY_FOR_MERGE.dependencies.toSet())
                },
                PENDING)
        }
        verify {
            gitHubClient.comment("subjectId",
                withArg {
                    assert(it.contains("\"replyTargetCommentId\":7"))
                    assert(it.contains("\"teamCityBuildId\":\"buildId\""))
                    assert(it.contains("\"teamCityBuildHeadRef\":\"headCommit\""))
                    assert(it.contains("OK, I've already triggered [${READY_FOR_MERGE.fullName} build](http://buildUrl) for you."))
                })
        }
    }

    @Test
    fun `say sorry to non-admin`() {
        // given
        every { pr.data.repository.pullRequest.comments.nodes } returns listOf(
            comment("""
          {
             "databaseId":3,
             "author":{
                "login":"bad-user"
             },
             "authorAssociation":"CONTRIBUTOR",
             "body":"@bot-gradle test ReadyForMerge plz"
          }""")
        )

        // when
        PullRequestContext(gitHubClient, teamCityClient, pr).executeCommentCommand(3)

        // then
        verify {
            gitHubClient.comment("subjectId",
                withArg {
                    assert(it.contains("\"replyTargetCommentId\":3"))
                    assert(it.contains("Sorry but I'm afraid you're not allowed to do this."))
                })
        }
    }

    @Test
    fun `display help message on help command`() {
        // given
        every { pr.data.repository.pullRequest.comments.nodes } returns listOf(
            comment("""
          {
             "databaseId":12345,
             "author":{
                "login":"jjohannes"
             },
             "authorAssociation":"MEMBER",
             "body":"@bot-gradle help"
          }""")
        )

        // when
        PullRequestContext(gitHubClient, teamCityClient, pr).executeCommentCommand(12345)

        // then
        verify {
            gitHubClient.comment("subjectId",
                withArg {
                    assert(it.contains("\"replyTargetCommentId\":12345"))
                    assert(it.contains("Currently I support the following commands"))
                })
        }
    }

    @Test
    fun `say sorry and display help message upon unknown command`() {
        // given
        every { pr.data.repository.pullRequest.comments.nodes } returns listOf(
            comment("""
          {
             "databaseId":12345,
             "author":{
                "login":"jjohannes"
             },
             "authorAssociation":"MEMBER",
             "body":"@bot-gradle balabala"
          }""")
        )

        // when
        PullRequestContext(gitHubClient, teamCityClient, pr).executeCommentCommand(12345)

        // then
        verify {
            gitHubClient.comment("subjectId",
                withArg {
                    assert(it.contains("\"replyTargetCommentId\":12345"))
                    assert(it.contains("Sorry I don't understand what you said"))
                })
        }
    }

    @Test
    fun `do nothing if the comment is already replied`() {
        // when
        every { pr.data.repository.pullRequest.comments.nodes } returns listOf(
            comment("""
          {
             "databaseId":1,
             "author":{
                "login":"jjohannes"
             },
             "authorAssociation":"MEMBER",
             "body":"@bot-gradle test ReadyForMerge plz"
          }"""),
            comment(""" 
          {
             "databaseId":2,
             "author":{
                "login":"bot-gradle"
             },
             "authorAssociation":"MEMBER",
             "body":"<!-- {\"replyTargetCommentId\":1,\"teamCityBuildId\":\"33016851\",\"teamCityBuildHeadRef\":\"018c8a143d536666e189ad662746f1103b778c02\"} -->\n            \nOK, I've already triggered [ReadyForMerge build](https://builds.gradle.org/viewLog.html?buildId=33016851) for you."
          }""")
        )
        // when
        PullRequestContext(gitHubClient, teamCityClient, pr).executeCommentCommand(1)

        // then
        excludeRecords { gitHubClient.whoAmI() }
        verifyGitHubTeamCityClientsNotCalled()
    }

    @Test
    fun `only update failure statuses`() {
        // given
        every { pr.data.repository.pullRequest.commits.nodes } returns listOf(
            objectMapper.readValue("""
                {
                  "commit": {
                    "commitUrl": "https://github.com/gradle/gradle/commit/1eda3eea31a06ddf3a3a5a969f0d00d46638a7ed",
                    "committedDate": "2018-02-22T03:20:23Z",
                    "oid": "1eda3eea31a06ddf3a3a5a969f0d00d46638a7ed",
                    "status": {
                      "state": "FAILURE",
                      "contexts": [
                        {
                          "state": "FAILURE",
                          "targetUrl": "https://builds.gradle.org/viewType.html?buildTypeId=Gradle_Check_Stage_ReadyforNightly_Trigger&branch_Gradle_Check=master&tab=buildTypeStatusDiv",
                          "description": "master branch failure since 2020-03-23T11:07:26.297Z",
                          "context": "CI Status"
                        },
                        {
                          "state": "SUCCESS",
                          "targetUrl": "https://url",
                          "description": "",
                          "context": "Compile All (Quick Feedback - Linux Only)"
                        },
                        {
                          "state": "SUCCESS",
                          "targetUrl": "https://url",
                          "description": "",
                          "context": "Sanity Check (Quick Feedback - Linux Only)"
                        },
                        {
                          "state": "FAILURE",
                          "targetUrl": "https://url",
                          "description": "",
                          "context": "Quick Feedback - Linux Only (Trigger) (Check)"
                        }
                      ]
                    }
                  }
                }
            """.trimIndent(), PullRequestWithCommentsResponse.Node2::class.java)
        )
        every { pr.data.repository.pullRequest.comments.nodes } returns listOf(
            comment("""
          {
             "databaseId":1,
             "author":{
                "login":"jjohannes"
             },
             "authorAssociation":"MEMBER",
             "body":"@bot-gradle test ReadyForMerge plz"
          }"""),
            comment(""" 
          {
             "databaseId":2,
             "author":{
                "login":"bot-gradle"
             },
             "authorAssociation":"MEMBER",
             "body":"<!-- {\"replyTargetCommentId\":1,\"teamCityBuildId\":\"33016851\",\"teamCityBuildHeadRef\":\"018c8a143d536666e189ad662746f1103b778c02\"} -->\n            \nOK, I've already triggered [ReadyForMerge build](https://builds.gradle.org/viewLog.html?buildId=33016851) for you."
          }"""),
            comment("""
          {
             "databaseId":7,
             "author":{
                "login":"jjohannes"
             },
             "authorAssociation":"MEMBER",
             "body":"@bot-gradle test ReadyForMerge plz"
          }  
            """)
        )
        val oldBuild = mockk<Build>()
        every { teamCityClient.findBuild("33016851") } returns Future.succeededFuture(oldBuild)
        every { oldBuild.state } returns BuildState.FINISHED
        every { oldBuild.id.stringId } returns "oldBuildId"
        every { oldBuild.getHomeUrl() } returns "http://oldBuildUrl"

        val newBuild = mockk<Build>()
        every { newBuild.id.stringId } returns "newBuildId"
        every { newBuild.getHomeUrl() } returns "http://newBuildUrl"
        every { pr.data.repository.pullRequest.headRef.name } returns "master"
        every { teamCityClient.triggerBuild(READY_FOR_MERGE, "master") } returns Future.succeededFuture(newBuild)

        // when
        PullRequestContext(gitHubClient, teamCityClient, pr).executeCommentCommand(7)

        // then
        verify { teamCityClient.triggerBuild(READY_FOR_MERGE, "master") }
        verify {
            gitHubClient.createCommitStatus(
                "gradle/gradle",
                "headCommit",
                withArg<Iterable<String>> {
                    assertEquals(it.toSet(),
                        setOf(
                            "Quick Feedback - Linux Only (Trigger) (Check)",
                            "Quick Feedback (Trigger) (Check)",
                            "Gradleception - Java8 Linux (Ready for Merge)",
                            "Smoke Tests with 3rd Party Plugins (instantSmokeTest) - Java14 Linux (Ready for Merge)",
                            "Smoke Tests with 3rd Party Plugins (instantSmokeTest) - Java8 Linux (Ready for Merge)",
                            "Smoke Tests with 3rd Party Plugins (smokeTest) - Java14 Linux (Ready for Merge)",
                            "Build Distributions (Ready for Merge)",
                            "Performance Regression Test Coordinator - Linux (Ready for Merge)",
                            "Ready for Merge (Trigger) (Check)"
                        )
                    )
                },
                PENDING)
        }
        verify {
            gitHubClient.comment("subjectId",
                withArg {
                    assert(it.contains("\"replyTargetCommentId\":7"))
                    assert(it.contains("\"teamCityBuildId\":\"newBuildId\""))
                    assert(it.contains("\"teamCityBuildHeadRef\":\"headCommit\""))
                    assert(it.contains("OK, I've already triggered [${READY_FOR_MERGE.fullName} build](http://newBuildUrl) for you."))
                })
        }
    }
}
