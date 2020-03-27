package org.gradle.bot.model

import org.gradle.bot.objectMapper

/*
{
  "data": {
    "addComment": {
      "clientMutationId": null,
      "subject": {
        "id": "MDExOlB1bGxSZXF1ZXN0MzkzNjEwMjA2"
      },
      "commentEdge": {
        "node": {
          "databaseId": 604768026
        }
      }
    }
  }
}
 */
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