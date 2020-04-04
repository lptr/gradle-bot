package org.gradle.bot.aop.logging

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Module
import com.google.inject.Singleton
import com.google.inject.matcher.AbstractMatcher
import com.google.inject.matcher.Matchers
import java.lang.RuntimeException
import java.lang.reflect.Method
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface testInterface {
    fun testMethod(arg1: String, arg2: Int): String
    fun testDoNoting()
    fun testException()
}

@Singleton
open class testInterfaceImpl @Inject constructor() : testInterface {

    @Log(LogLevel.INFO)
    override fun testDoNoting() {
    }

    @Log(LogLevel.WARN)
    override fun testMethod(arg1: String, arg2: Int): String = "$arg1$arg2"

    @Log
    override fun testException() {
        throw RuntimeException()
    }
}

@ExtendWith(MockitoExtension::class)
class TestLog {

    @Spy
    var logger: Logger = LoggerFactory.getLogger("[JUST FOR TEST] " + TestLog::class.java.simpleName)

    @Captor
    lateinit var stringCaptor: ArgumentCaptor<String>

    @Captor
    lateinit var argumentsCaptor: ArgumentCaptor<Any>

    lateinit var injector: Injector

    private val debugLogMethodInterceptor = LogMethodInterceptor()

    lateinit var instance: testInterface

    @BeforeEach
    fun setup() {
        injector = Guice.createInjector(Module {
            it.bind(testInterface::class.java).to(testInterfaceImpl::class.java)
            it.bindInterceptor(Matchers.any(), object : AbstractMatcher<Method>() {
                override fun matches(t: Method): Boolean {
                    return t.isAnnotationPresent(Log::class.java) && !t.isSynthetic
                }
            }, debugLogMethodInterceptor)
        })
        instance = injector.getInstance(testInterface::class.java)
        val instanceLogger = debugLogMethodInterceptor.javaClass.getDeclaredField("logger")
        instanceLogger.isAccessible = true
        instanceLogger.set(debugLogMethodInterceptor, logger)
    }

    @Test
    fun `test aop warn log with args and return`() {
        val result = instance.testMethod("hello", 1)

        verify(logger, times(1)).warn(stringCaptor.capture(),
                argumentsCaptor.capture(), argumentsCaptor.capture(), argumentsCaptor.capture())
        verify(logger, times(1)).warn(stringCaptor.capture(),
                argumentsCaptor.capture(), argumentsCaptor.capture(), argumentsCaptor.capture(), argumentsCaptor.capture())

        Assertions.assertTrue(stringCaptor.allValues[0].startsWith("Entering method"))
        Assertions.assertTrue(stringCaptor.allValues[1].startsWith("Exiting method"))

        // parameter(s)
        Assertions.assertNotNull(argumentsCaptor.allValues[2])
        // method
        Assertions.assertTrue(argumentsCaptor.allValues[0] == argumentsCaptor.allValues[3])
        // class
        Assertions.assertTrue(argumentsCaptor.allValues[1] == argumentsCaptor.allValues[4])
        // spent
        Assertions.assertNotNull(argumentsCaptor.allValues[5])
        // return
        Assertions.assertEquals(result, (argumentsCaptor.allValues[6]))
    }

    @Test
    fun `test aop info log do nothing`() {
        instance.testDoNoting()

        verify(logger, times(1)).info(stringCaptor.capture(),
                argumentsCaptor.capture(), argumentsCaptor.capture(), argumentsCaptor.capture())
        verify(logger, times(1)).info(stringCaptor.capture(),
                argumentsCaptor.capture(), argumentsCaptor.capture(), argumentsCaptor.capture(), argumentsCaptor.capture())

        Assertions.assertTrue(stringCaptor.allValues[0].startsWith("Entering method"))
        Assertions.assertTrue(stringCaptor.allValues[1].startsWith("Exiting method"))

        // parameter(s)
        Assertions.assertEquals("[]", argumentsCaptor.allValues[2])
        // method
        Assertions.assertTrue(argumentsCaptor.allValues[0] == argumentsCaptor.allValues[3])
        // class
        Assertions.assertTrue(argumentsCaptor.allValues[1] == argumentsCaptor.allValues[4])
        // spent
        Assertions.assertNotNull(argumentsCaptor.allValues[5])
        // return
        Assertions.assertNull(argumentsCaptor.allValues[6])
    }

    @Test
    fun `test aop debug log throw exception`() {
        `when`(logger.isDebugEnabled).thenReturn(true)

        instance.testException()
        verify(logger, times(1)).debug(stringCaptor.capture(),
                argumentsCaptor.capture(), argumentsCaptor.capture(), argumentsCaptor.capture())
        verify(logger, times(1)).debug(stringCaptor.capture(),
                argumentsCaptor.capture(), argumentsCaptor.capture(), argumentsCaptor.capture(), argumentsCaptor.capture())

        Assertions.assertTrue(stringCaptor.allValues[0].startsWith("Entering method"))
        Assertions.assertTrue(stringCaptor.allValues[1].startsWith("Exiting method"))

        // parameter(s)
        Assertions.assertEquals("[]", argumentsCaptor.allValues[2])
        // method
        Assertions.assertTrue(argumentsCaptor.allValues[0] == argumentsCaptor.allValues[3])
        // class
        Assertions.assertTrue(argumentsCaptor.allValues[1] == argumentsCaptor.allValues[4])
        // spent
        Assertions.assertNotNull(argumentsCaptor.allValues[5])
        // exception
        Assertions.assertTrue((argumentsCaptor.allValues[6] as String).contains("Exception"))
    }
}
