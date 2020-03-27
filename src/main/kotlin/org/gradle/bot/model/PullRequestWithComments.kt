package org.gradle.bot.model

import com.fasterxml.jackson.annotation.JsonProperty


/*

query {
  repository(owner:"gradle", name: "gradle") {
    pullRequest(number: 12595) {
      body

      url

      headRef {
        target {
          oid
        }
        repository {
          isFork
          owner {
            login
          }
          name
        }
        name
      }
      baseRefName
      comments(first: 100) {
        nodes {
          databaseId
          author {
            login
          }
          authorAssociation
          body
        }
      }
      commits(last: 1) {
          nodes {
            commit {
              commitUrl
              oid
              status {
                state

                contexts {
                  state
                  targetUrl
                  description
                  context
                }
              }
            }
          }
        }
    }
  }
}


{
  "data": {
    "repository": {
      "pullRequest": {
        "body": "on a failing build caused by instant execution problems, build failure details problems\r\non a failing build *not* caused by instant execution problems, warning log details problems\r\non a succeeding build, warning log details problems\r\n\r\nin all cases, generate the HTML report if any problem\r\n\r\n----\r\n\r\nMost of the impact on annotated tests can be seen in https://github.com/gradle/gradle/pull/12505 which is based on this very PR.",
        "url": "https://github.com/gradle/gradle/pull/12595",
        "headRef": {
          "target": {
            "oid": "2e436a09b8f0b614ee842b18aab89c245addb681"
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
              "databaseId": 604667869,
              "author": {
                "login": "eskatos"
              },
              "authorAssociation": "MEMBER",
              "body": "@adammurdoch, @bamboo, this is ready for another round\r\n\r\nPlease consider trying it out locally to experience the error/problems reporting."
            },
            {
              "databaseId": 604713694,
              "author": {
                "login": "eskatos"
              },
              "authorAssociation": "MEMBER",
              "body": "False alarm. Turns out it needs a bit more work. There's https://github.com/gradle/gradle/pull/12595#discussion_r398912241 which should also help fix an issue with state discarding vs. `failOnProblems` I just found playing a bit more with it. Moved back to _in progress_.\r\n"
            }
          ]
        },
        "commits": {
          "nodes": [
            {
              "commit": {
                "commitUrl": "https://github.com/gradle/gradle/commit/2e436a09b8f0b614ee842b18aab89c245addb681",
                "oid": "2e436a09b8f0b614ee842b18aab89c245addb681",
                "status": {
                  "state": "SUCCESS",
                  "contexts": [
                    {
                      "state": "SUCCESS",
                      "targetUrl": "https://builds.gradle.org/viewLog.html?buildId=32991160&buildTypeId=Gradle_Check_BuildDistributions",
                      "description": "TeamCity build finished",
                      "context": "Build Distributions (Ready for Merge)"
                    },
                    {
                      "state": "SUCCESS",
                      "targetUrl": "https://builds.gradle.org/viewLog.html?buildId=32991056&buildTypeId=Gradle_Check_CompileAll",
                      "description": "TeamCity build finished",
                      "context": "Compile All (Quick Feedback - Linux Only)"
                    },
                    {
                      "state": "SUCCESS",
                      "targetUrl": "https://builds.gradle.org/viewLog.html?buildId=32991161&buildTypeId=Gradle_Check_Gradleception",
                      "description": "TeamCity build finished",
                      "context": "Gradleception - Java8 Linux (Ready for Merge)"
                    },
                    {
                      "state": "SUCCESS",
                      "targetUrl": "https://builds.gradle.org/viewLog.html?buildId=32991165&buildTypeId=Gradle_Check_PerformanceTestCoordinator",
                      "description": "TeamCity build finished",
                      "context": "Performance Regression Test Coordinator - Linux (Ready for Merge)"
                    },
                    {
                      "state": "SUCCESS",
                      "targetUrl": "https://builds.gradle.org/viewLog.html?buildId=32991159&buildTypeId=Gradle_Check_Stage_QuickFeedback_Trigger",
                      "description": "TeamCity build finished",
                      "context": "Quick Feedback (Trigger) (Check)"
                    },
                    {
                      "state": "SUCCESS",
                      "targetUrl": "https://builds.gradle.org/viewLog.html?buildId=32991108&buildTypeId=Gradle_Check_Stage_QuickFeedbackLinuxOnly_Trigger",
                      "description": "TeamCity build finished",
                      "context": "Quick Feedback - Linux Only (Trigger) (Check)"
                    },
                    {
                      "state": "SUCCESS",
                      "targetUrl": "https://builds.gradle.org/viewLog.html?buildId=32991316&buildTypeId=Gradle_Check_Stage_ReadyforMerge_Trigger",
                      "description": "TeamCity build finished",
                      "context": "Ready for Merge (Trigger) (Check)"
                    },
                    {
                      "state": "SUCCESS",
                      "targetUrl": "https://builds.gradle.org/viewLog.html?buildId=32991395&buildTypeId=Gradle_Check_Stage_ReadyforNightly_Trigger",
                      "description": "TeamCity build finished",
                      "context": "Ready for Nightly (Trigger) (Check)"
                    },
                    {
                      "state": "SUCCESS",
                      "targetUrl": "https://builds.gradle.org/viewLog.html?buildId=32991057&buildTypeId=Gradle_Check_SanityCheck",
                      "description": "TeamCity build finished",
                      "context": "Sanity Check (Quick Feedback - Linux Only)"
                    },
                    {
                      "state": "SUCCESS",
                      "targetUrl": "https://builds.gradle.org/viewLog.html?buildId=32991163&buildTypeId=Gradle_Check_InstantSmokeTestsJava14",
                      "description": "TeamCity build finished",
                      "context": "Smoke Tests with 3rd Party Plugins (instantSmokeTest) - Java14 Linux (Ready for Merge)"
                    },
                    {
                      "state": "SUCCESS",
                      "targetUrl": "https://builds.gradle.org/viewLog.html?buildId=32991164&buildTypeId=Gradle_Check_InstantSmokeTestsJava8",
                      "description": "TeamCity build finished",
                      "context": "Smoke Tests with 3rd Party Plugins (instantSmokeTest) - Java8 Linux (Ready for Merge)"
                    },
                    {
                      "state": "SUCCESS",
                      "targetUrl": "https://builds.gradle.org/viewLog.html?buildId=32991162&buildTypeId=Gradle_Check_SmokeTestsJava14",
                      "description": "TeamCity build finished",
                      "context": "Smoke Tests with 3rd Party Plugins (smokeTest) - Java14 Linux (Ready for Merge)"
                    }
                  ]
                }
              }
            }
          ]
        }
      }
    }
  }
}
 */
fun pullRequestsWithCommentsQuery(owner: String, name: String, number: Int, maxCommentNum: Int = 100) = """
query {
  repository(owner:"$owner", name: "$name") { 
    pullRequest(number: $number) {
      body 
      
      url
      
      headRef {
        target {
          oid
        }
        repository {
          isFork
          owner {
            login
          }
          name
        }
        name
      }
      baseRefName
      comments(first: $maxCommentNum) {
        nodes {
          databaseId
          author {
            login
          }
          authorAssociation
          body
        }
      }
      commits(last: 1) {
          nodes {
            commit {
              commitUrl
              oid
              status {
                state

                contexts {
                  state
                  targetUrl
                  description
                  context
                }
              }
            }
          }
        }
    }
  }
}  
""".trimIndent().replace('\n',' ')

data class PullRequestWithComments(
    @JsonProperty("data")
    var `data`: Data
) {
    data class Data(
        @JsonProperty("repository")
        var repository: Repository
    ) {
        data class Repository(
            @JsonProperty("pullRequest")
            var pullRequest: PullRequest
        ) {
            data class PullRequest(
                @JsonProperty("baseRefName")
                var baseRefName: String,
                @JsonProperty("body")
                var body: String,
                @JsonProperty("comments")
                var comments: Comments,
                @JsonProperty("commits")
                var commits: Commits,
                @JsonProperty("headRef")
                var headRef: HeadRef,
                @JsonProperty("url")
                var url: String
            ) {
                data class Comments(
                    @JsonProperty("nodes")
                    var nodes: List<Node>
                ) {
                    data class Node(
                        @JsonProperty("author")
                        var author: Author,
                        @JsonProperty("authorAssociation")
                        var authorAssociation: String,
                        @JsonProperty("body")
                        var body: String,
                        @JsonProperty("databaseId")
                        var databaseId: Int
                    ) {
                        data class Author(
                            @JsonProperty("login")
                            var login: String
                        )
                    }
                }

                data class Commits(
                    @JsonProperty("nodes")
                    var nodes: List<Node>
                ) {
                    data class Node(
                        @JsonProperty("commit")
                        var commit: Commit
                    ) {
                        data class Commit(
                            @JsonProperty("commitUrl")
                            var commitUrl: String,
                            @JsonProperty("oid")
                            var oid: String,
                            @JsonProperty("status")
                            var status: Status
                        ) {
                            data class Status(
                                @JsonProperty("contexts")
                                var contexts: List<Context>,
                                @JsonProperty("state")
                                var state: String
                            ) {
                                data class Context(
                                    @JsonProperty("context")
                                    var context: String,
                                    @JsonProperty("description")
                                    var description: String,
                                    @JsonProperty("state")
                                    var state: String,
                                    @JsonProperty("targetUrl")
                                    var targetUrl: String
                                )
                            }
                        }
                    }
                }

                data class HeadRef(
                    @JsonProperty("name")
                    var name: String,
                    @JsonProperty("repository")
                    var repository: Repository,
                    @JsonProperty("target")
                    var target: Target
                ) {
                    data class Repository(
                        @JsonProperty("isFork")
                        var isFork: Boolean,
                        @JsonProperty("name")
                        var name: String,
                        @JsonProperty("owner")
                        var owner: Owner
                    ) {
                        data class Owner(
                            @JsonProperty("login")
                            var login: String
                        )
                    }

                    data class Target(
                        @JsonProperty("oid")
                        var oid: String
                    )
                }
            }
        }
    }
}

