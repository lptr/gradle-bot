package org.gradle.bot.webhookhandlers

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import javax.inject.Inject
import javax.inject.Singleton
import org.gradle.bot.endWithJson

@Singleton
class SlackWebHookHandler @Inject constructor(private val vertx: Vertx) : Handler<RoutingContext> {
    override fun handle(context: RoutingContext?) {
        context?.response()?.endWithJson(emptyMap<String, Any>())
    }
}
