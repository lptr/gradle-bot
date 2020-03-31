package org.gradle.bot.eventhandlers

import io.vertx.core.Handler
import io.vertx.core.eventbus.Message


/**
 * Handle a webhook event via Vert.x event bus.
 * The message address is {eventPrefix}.{eventType}
 *
 * For example,
 *
 * github.status
 * github.issue_comment
 * teamcity.build
 */
interface WebHookEventHandler : Handler<Message<String>> {
    val eventPrefix: String
    val eventType: String
    val eventAddress
        get() = "${eventPrefix}.$eventType"
}

