package org.gradle.bot.model

import org.gradle.bot.objectMapper

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
