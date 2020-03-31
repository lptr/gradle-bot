package org.gradle.bot.eventhandlers.github.issuecomment

import io.vertx.core.Future
import org.gradle.bot.client.GitHubClient
import org.gradle.bot.client.TeamCityClient
import org.gradle.bot.model.BuildStage
import org.gradle.bot.model.CommitStatusState
import org.gradle.bot.model.PullRequestWithCommentsResponse
import org.gradle.bot.objectMapper
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildId
import org.jetbrains.teamcity.rest.BuildState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.lenient
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PullRequestContextTest {
    @Mock
    lateinit var gitHubClient: GitHubClient
    @Mock
    lateinit var teamCityClient: TeamCityClient
    @Mock
    lateinit var context: PullRequestContext
    // The build in comment metadata, the previously triggered build
    @Mock
    lateinit var oldBuild: Build
    @Mock
    lateinit var oldBuildId: BuildId
    private val oldBuildIdString = "33016851"
    private val oldBuildHomeUrl = "http://$oldBuildIdString"

    // The newly triggered build
    @Mock
    lateinit var newBuild: Build
    @Mock
    lateinit var newBuildId: BuildId
    private val newBuildIdString = "newBuildId"
    private val newBuildHomeUrl = "http://$newBuildIdString"

    private val botName = "bot-gradle"
    private val repoName = "gradle/gradle"
    private val pullRequestSubjectId = "MDExOlB1bGxSZXF1ZXN0MzkxNjM4MTkw"
    private val firstTeamCityBuildHeadRef = "018c8a143d536666e189ad662746f1103b778c02"
    private val pullRequestBranch = "eskatos/ie/rework-error-problem-handling"

    @BeforeEach
    fun setUp() {
        `when`(gitHubClient.whoAmI()).thenReturn(botName)
        context = PullRequestContext(gitHubClient, teamCityClient, pullRequestWithCommentsResponse)

        lenient().`when`(gitHubClient.comment(anyString(), anyString())).thenReturn(Future.succeededFuture(null))

        lenient().`when`(teamCityClient.findBuild(oldBuildIdString)).thenReturn(Future.succeededFuture(oldBuild))
        lenient().`when`(oldBuild.state).thenReturn(BuildState.FINISHED)
        lenient().`when`(oldBuild.getHomeUrl()).thenReturn(oldBuildHomeUrl)

        lenient().`when`(teamCityClient.triggerBuild(any(), anyString())).thenReturn(Future.succeededFuture(newBuild))
        lenient().`when`(newBuild.id).thenReturn(newBuildId)
        lenient().`when`(newBuildId.stringId).thenReturn(newBuildIdString)
        lenient().`when`(newBuild.getHomeUrl()).thenReturn(newBuildHomeUrl)
    }

    @Test
    fun `can parse comments and commands in the comments`() {
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
        assertEquals(CommentMetadata(1, oldBuildIdString, firstTeamCityBuildHeadRef), context.comments[1].metadata)
        context.comments.filterIndexed { index, _ -> index != 1 }.forEach { assertEquals(CommentMetadata(null, null, null), it.metadata) }
        assertFalse((context.comments[2] as CommandComment).isAdmin)
    }

    @Test
    fun `execute commands from admin then reply`() {
        // given
        val captor: MyArgumentCaptor<String> = MyArgumentCaptor.forClass(String::class.java)
        // Command from admin
        context.processCommand(7)

        // then
        verify(teamCityClient).findBuild(oldBuildIdString)
        verify(teamCityClient).triggerBuild(BuildStage.READY_FOR_MERGE, pullRequestBranch)
        verify(gitHubClient).createCommitStatus(repoName, firstTeamCityBuildHeadRef, BuildStage.READY_FOR_MERGE.dependencies, CommitStatusState.PENDING)
        verify(gitHubClient).comment(captor.capture(), captor.capture())
        assertEquals(pullRequestSubjectId, captor.allValues[0])

        println(captor.allValues[1])

        assertTrue(captor.allValues[1].contains("""<!-- {"replyTargetCommentId":7,"teamCityBuildId":"newBuildId","teamCityBuildHeadRef":"018c8a143d536666e189ad662746f1103b778c02"} -->"""))
        assertTrue(captor.allValues[1].contains("OK, I've already triggered [${BuildStage.READY_FOR_MERGE.fullName} build]($newBuildHomeUrl) for you."))
    }

    @Test
    fun `say sorry to non-admin`() {
        // Command from non-admin
        context.processCommand(2)
    }

    @Test
    fun `reply contact administrator upon errors`() {
    }

    @Test
    fun `display help message on help command`() {
    }

    @Test
    fun `say sorry and display help message upon unknown command`() {
    }

    @Test
    fun `does not trigger new build if current build is running`() {
    }

    @Test
    fun `do nothing if the comment is already replied`() {
    }

    @Test
    fun `do nothing if the comment is deleted`() {
    }
}

private fun <T> any(): T {
    return Mockito.any<T>()
}

private val pullRequestWithCommentsResponse = objectMapper.readValue("""
    {
  "data": {
    "repository": {
      "nameWithOwner": "gradle/gradle",
      "pullRequest": {
        "id": "MDExOlB1bGxSZXF1ZXN0MzkxNjM4MTkw",
        "body": "on a failing build caused by state serialization errors, state is discarded, error is detailled in build failure and summary of problems seen so far in build output\r\non a failing build caused by instant execution problems, state is discarded and problem summary in build failure\r\non a failing build *not* caused by instant execution problems, problem summary in build output\r\non a succeeding build, problem summary in build output\r\n\r\nin all cases, generate the HTML report if any problem\r\n\r\n----\r\n\r\nMost of the impact on annotated tests can be seen in https://github.com/gradle/gradle/pull/12505 which is based on this very PR.",
        "url": "https://github.com/gradle/gradle/pull/12595",
        "headRef": {
          "target": {
            "oid": "018c8a143d536666e189ad662746f1103b778c02"
          },
          "repository": {
            "isFork": false,
            "owner": {
              "login": "gradle"
            },
            "name": "gradle"
          },
          "name": "eskatos/ie/rework-error-problem-handling"
        },
        "baseRefName": "master",
        "comments": {
          "nodes": [
                          {
                             "databaseId":1,
                             "author":{
                                "login":"jjohannes"
                             },
                             "authorAssociation":"MEMBER",
                             "body":"@bot-gradle test ReadyForMerge plz"
                          },
                          {
                             "databaseId":2,
                             "author":{
                                "login":"bot-gradle"
                             },
                             "authorAssociation":"MEMBER",
                             "body":"<!-- {\"replyTargetCommentId\":1,\"teamCityBuildId\":\"33016851\",\"teamCityBuildHeadRef\":\"018c8a143d536666e189ad662746f1103b778c02\"} -->\n            \nOK, I've already triggered [ReadyForMerge build](https://builds.gradle.org/viewLog.html?buildId=33016851) for you."
                          },
                          {
                             "databaseId":3,
                             "author":{
                                "login":"bad-user"
                             },
                             "authorAssociation":"CONTRIBUTOR",
                             "body":"@bot-gradle test ReadyForMerge plz"
                          },
                          {
                             "databaseId":4,
                             "author":{
                                "login":"bot-gradle"
                             },
                             "authorAssociation":"MEMBER",
                             "body":"[Your build](theUrl) failed."
                          },
                          {
                             "databaseId":5,
                             "author":{
                                "login":"passerby"
                             },
                             "authorAssociation":"CONTRIBUTOR",
                             "body":"This is an unrelated comment"
                          },
                          {
                             "databaseId":6,
                             "author":{
                                "login":"collaborator"
                             },
                             "authorAssociation":"COLLABORATOR",
                             "body":"This is an unrelated comment"
                          },
                          {
                             "databaseId":7,
                             "author":{
                                "login":"jjohannes"
                             },
                             "authorAssociation":"MEMBER",
                             "body":"@bot-gradle test ReadyForMerge plz"
                          }  
          ]
        },
        "commits": {
          "nodes": [
            {
              "commit": {
                "commitUrl": "https://github.com/gradle/gradle/commit/63aaca68d485c76c4f9c11be9098348746cde04a",
                "oid": "63aaca68d485c76c4f9c11be9098348746cde04a",
                "status": null 
              }
            }
          ]
        }
      }
    }
  }
}
    """, PullRequestWithCommentsResponse::class.java)

class MyArgumentCaptor<T>(private val delegate: ArgumentCaptor<T>) {
    companion object {
        fun <T> forClass(clazz: Class<T>): MyArgumentCaptor<T> {
            return MyArgumentCaptor(ArgumentCaptor.forClass(clazz))
        }
    }

    fun capture(): T = delegate.capture()
    val value: T
        get() = delegate.value
    val allValues: List<T>
        get() = delegate.allValues
}

// inline fun <reified T : Any> argumentCaptor() = ArgumentCaptor.forClass(T::class.java)
//
// private fun <T> uninitialized(): T = null as T
