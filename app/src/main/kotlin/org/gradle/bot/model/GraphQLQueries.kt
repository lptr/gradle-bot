package org.gradle.bot.model

import org.gradle.bot.objectMapper

fun pullRequestsWithCommentsQuery(owner: String, name: String, number: Long, maxCommentNum: Int = 100) = """
query {
  repository(owner: "$owner", name: "$name") {
    nameWithOwner
    pullRequest(number: $number) {
      id
      number
      body
      url
      author {
        login
      }
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
      reviews(first: 100) {
        nodes {
          databaseId
          author {
            login
          }
          body
          state
        }
      }
      commits(last: 1) {
        nodes {
          commit {
            commitUrl
            committedDate
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

""".trimIndent().replace('\n', ' ')

fun addCommentMutation(subjectId: String, commentBody: String) = """
mutation {            
  addComment(input:{subjectId:"$subjectId",body:${objectMapper.writeValueAsString(commentBody)}}) {
    clientMutationId
    subject {
      id
    }
    commentEdge {
      node {
        databaseId
      }
    }
  }
}  
        """.replace('\n', ' ')

val whoAmIQuery = """
query {    
  viewer {
    login
    name
  } 
}  
""".trimIndent().replace('\n', ' ')

fun listOpenPullRequestsQuery(owner: String, name: String, maxPrNum: Int = 100) = """
query {
  repository(owner: "$owner", name: "$name") {
    owner {
      login
    }
    name
    pullRequests(states: OPEN, first: $maxPrNum) {
      nodes {
        id
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
        commits(last: 1) {
          nodes {
            commit {
              commitUrl
              committedDate
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
}

""".trimIndent()
