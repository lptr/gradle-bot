package org.gradle.bot.eventhandlers.github.issuecomment

//import org.gradle.bot.client.GitHubClient
//import org.gradle.bot.client.TeamCityClient
//import org.gradle.bot.model.IssueCommentGitHubEvent
//import org.gradle.bot.objectMapper
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.ArgumentMatchers.anyLong
//import org.mockito.ArgumentMatchers.anyString
//import org.mockito.Mock
//import org.mockito.Mockito
//import org.mockito.junit.jupiter.MockitoExtension
//
//@ExtendWith(MockitoExtension::class)
//class PullRequestCommentEventHandlerTest {
//    @Mock
//    lateinit var gitHubClient: GitHubClient
//    @Mock
//    lateinit var teamCityClient: TeamCityClient
//
//    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
//    @Test
//    fun `skips comments on issue`() {
//        // given
//        val json = javaClass.classLoader.getResourceAsStream("modeljson/IssueCommentGitHubEvent/IssueCommentEvent_without_pullRequest.json").reader().readText()
//        val event = objectMapper.readValue(json, IssueCommentGitHubEvent::class.java)
//        // when
//        PullRequestCommentEventHandler(gitHubClient, teamCityClient).handleEvent(event)
//        // then
//        Mockito.verify(gitHubClient, Mockito.times(0)).getPullRequestWithComments(anyString(), anyLong())
//    }
//}
