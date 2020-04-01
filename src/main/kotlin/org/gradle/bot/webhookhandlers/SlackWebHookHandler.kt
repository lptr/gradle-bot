package org.gradle.bot.webhookhandlers

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import javax.inject.Inject
import javax.inject.Singleton
import org.gradle.bot.objectMapper

// https://api.slack.com/events/url_verification
/*
{
    "token": "Jhj5dZrVaK7ZwHHjRyZWjbDl",
    "challenge": "3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P",
    "type": "url_verification"
}
 */

data class SlackChallenge(
    @JsonProperty("token") val token: String,
    @JsonProperty("challenge") val challenge: String,
    @JsonProperty("type") val type: String
)

@Singleton
class SlackWebHookHandler @Inject constructor(private val vertx: Vertx) : Handler<RoutingContext> {
    override fun handle(context: RoutingContext?) {
        context?.let {
            val json = objectMapper.readValue(it.bodyAsString, SlackChallenge::class.java)
            context.response().putHeader("Content-Type", "text/plain").end(json.challenge)
        }
    }
}
