package org.gradle.bot.model

import com.fasterxml.jackson.annotation.JsonProperty


/*
{
  "data": {
    "repository": {
      "pullRequest": {
        "body": "This enhance docs around `ivy-component-metadata-rule` to prevent breaking Maven based modules\r\n\r\nIf check for `IvyModuleDescriptor` is not in place, the rule will fail with maven based modules causing issues such as:\r\n\r\n```\r\nExecution failed for task ':compileJava'.\r\n> Could not resolve all files for configuration ':compileClasspath'.\r\n   > Could not resolve com.google.guava:guava:19.0.\r\n     Required by:\r\n         project : > foo:my-core:1.0.0\r\n      > Cannot choose between the following variants of com.google.guava:guava:19.0:\r\n          - apiElements\r\n          - compile\r\n          - runtime\r\n        All of them match the consumer attributes:\r\n          - Variant 'apiElements' capability com.google.guava:guava:19.0:\r\n              - Unmatched attributes:\r\n                  - Required org.gradle.dependency.bundling 'external' but no value provided.\r\n``` \r\n\r\n### Contributor Checklist\r\n- [x] [Review Contribution Guidelines](https://github.com/gradle/gradle/blob/master/CONTRIBUTING.md)\r\n- [x] Make sure that all commits are [signed off](https://git-scm.com/docs/git-commit#git-commit---signoff) to indicate that you agree to the terms of [Developer Certificate of Origin](https://developercertificate.org/).\r\n- [x] Check [\"Allow edit from maintainers\" option](https://help.github.com/articles/allowing-changes-to-a-pull-request-branch-created-from-a-fork/) in pull request so that additional changes can be pushed by Gradle team\r\n- [ ] Provide integration tests (under `<subproject>/src/integTest`) to verify changes from a user perspective\r\n- [ ] Provide unit tests (under `<subproject>/src/test`) to verify logic\r\n- [ ] Update User Guide, DSL Reference, and Javadoc for public-facing changes\r\n- [ ] Ensure that tests pass locally: `./gradlew <changed-subproject>:check`\r\n\r\n### Gradle Core Team Checklist\r\n- [ ] Verify design and implementation \r\n- [ ] Verify test coverage and CI build status\r\n- [ ] Verify documentation\r\n- [ ] Recognize contributor in release notes\r\n",
        "headRef": {
          "target": {
            "oid": "b1b94ef77d1b41565c29c1df2b8df2c1fab44aa4"
          },
          "repository": {
            "isFork": true,
            "owner": {
              "login": "rpalcolea"
            },
            "name": "gradle"
          },
          "name": "ivy-component-metadata-rule/check-for-IvyModuleDescriptor"
        },
        "baseRefName": "master",
        "comments": {
          "nodes": [
            {
              "databaseId": 602524371,
              "author": {
                "login": "ljacomet"
              },
              "authorAssociation": "MEMBER",
              "body": "And in addition, please target `master` for this instead of `release`."
            },
            {
              "databaseId": 602711239,
              "author": {
                "login": "rpalcolea"
              },
              "authorAssociation": "CONTRIBUTOR",
              "body": "> And in addition, please target `master` for this instead of `release`.\r\n\r\nHey @ljacomet , added the comments and changed it to target `master`."
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
                @JsonProperty("headRef")
                var headRef: HeadRef
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