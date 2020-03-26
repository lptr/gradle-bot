package org.gradle.bot.model
import com.fasterxml.jackson.annotation.JsonProperty


/*
{
  "data": {
    "viewer": {
      "login": "torvalds",
      "name": "Linus Torvalds"
    }
  }
}
 */

val whoAmIQuery="""
  viewer {
    login
    name
  } 
""".trimIndent()

data class WhoAmI(
    @JsonProperty("data")
    var `data`: Data
) {
    data class Data(
        @JsonProperty("viewer")
        var viewer: Viewer
    ) {
        data class Viewer(
            @JsonProperty("login")
            var login: String,
            @JsonProperty("name")
            var name: String
        )
    }
}
