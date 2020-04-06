package org.gradle.bot.aop.logging

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Target(AnnotationTarget.FUNCTION)
annotation class Log(val level: LogLevel = LogLevel.DEBUG)

enum class LogLevel {
    TRACE,
    DEBUG,
    WARN,
    INFO,
    ERROR
}

class LogMethodInterceptor : MethodInterceptor {
    val logger: Logger = LoggerFactory.getLogger(LogMethodInterceptor::class.java)

    override fun invoke(invocation: MethodInvocation): Any? {
        val level = invocation.method.getAnnotation(Log::class.java).level

        val methodClass: String = invocation.method.declaringClass.name
        val methodName: String = invocation.method.name
        val args: String = invocation.arguments.joinToString(",", "[", "]",
                transform = { arg -> arg::class.java.simpleName + ": " + arg.toString() })

        getLogger(level, "Entering method {} of class {}, the arguments is {}",
                methodName, methodClass, args)

        val startTime: Long = System.nanoTime()

        var exception: Throwable? = null
        var methodResult: Any? = null

        try {
            methodResult = invocation.proceed()
        } catch (e: Throwable) {
            exception = e
        } finally {
            val totalTime = System.nanoTime() - startTime
            if (exception != null) {
                getLogger(level, "Exiting method {} of class {}, total time is {} ms, return {}",
                        methodName, methodClass, toString(totalTime), toString(exception))
            } else {
                getLogger(level, "Exiting method {} of class {}, total time is {} ms, return {}",
                        methodName, methodClass, toString(totalTime), methodResult)
            }
        }
        return methodResult
    }

    private fun getLogger(level: LogLevel, msg: String, vararg params: Any?) =
            when (level) {
                LogLevel.TRACE -> if (logger.isTraceEnabled) logger.trace(msg, *params) else {}
                LogLevel.DEBUG -> if (logger.isDebugEnabled) logger.debug(msg, *params) else {}
                LogLevel.WARN -> logger.warn(msg, *params)
                LogLevel.INFO -> logger.info(msg, *params)
                LogLevel.ERROR -> logger.error(msg, *params)
            }

    private fun toString(e: Throwable): String = "[" + e.javaClass.simpleName + "]" + e.message

    private fun toString(nanoseconds: Long): String = String.format("%.3f", nanoseconds / 1000_000.0)
}
