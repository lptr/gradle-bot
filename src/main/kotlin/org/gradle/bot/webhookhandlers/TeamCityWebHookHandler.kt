package org.gradle.bot.webhookhandlers

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import org.gradle.bot.endWithJson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamCityWebHookHandler @Inject constructor(private val vertx: Vertx) : Handler<RoutingContext> {
    override fun handle(context: RoutingContext?) {
        context.parseTeamCityPayload()?.also {
            vertx.eventBus().publish("teamcity.build", it)
        }
        context?.response()?.endWithJson(emptyMap<String, Any>())
    }
}

private fun RoutingContext?.parseTeamCityPayload(): String? {
    return this?.let {
        logger.debug("Get TeamCity webhook {}", bodyAsString)
        bodyAsString
    }
}
