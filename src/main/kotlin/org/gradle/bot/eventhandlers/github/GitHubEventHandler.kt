package org.gradle.bot.eventhandlers.github

import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import java.lang.reflect.ParameterizedType
import org.gradle.bot.model.GitHubEvent
import org.gradle.bot.objectMapper

interface GitHubEventHandler : Handler<Message<String>> {
    val eventType: String
}

@Suppress("UNCHECKED_CAST")
abstract class AbstractGitHubEventHandler<T : GitHubEvent> : GitHubEventHandler {
    private val eventClass by lazy {
        val superClass = javaClass.genericSuperclass
        (superClass as ParameterizedType).actualTypeArguments[0] as Class<T>
    }
    override val eventType: String by lazy {
        GitHubEvent.EVENT_TYPES.inverse().getValue(eventClass)
    }

    override fun handle(event: Message<String>?) {
        handleEvent(objectMapper.readValue(event?.body(), eventClass))
    }

    abstract fun handleEvent(event: T)
}
