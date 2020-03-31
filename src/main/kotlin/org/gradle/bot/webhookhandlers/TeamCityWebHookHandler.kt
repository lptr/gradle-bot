package org.gradle.bot.webhookhandlers

import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import javax.inject.Inject
import javax.inject.Singleton
import org.gradle.bot.endWithJson

@Singleton
class TeamCityWebHookHandler @Inject constructor() : Handler<RoutingContext> {
    override fun handle(context: RoutingContext?) {
        context.parseTeamCityPayload()
        context?.response()?.endWithJson(emptyMap<String, Any>())
    }
}

private fun RoutingContext?.parseTeamCityPayload(): String? {
    return this?.let {
        logger.debug("Get TeamCity webhook {}", bodyAsString)
        bodyAsString
    }
}
