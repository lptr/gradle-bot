package org.gradle.bot.integration

//import com.google.inject.Injector
//import io.vertx.core.Future
//import io.vertx.core.Verticle
//import io.vertx.core.Vertx
//import java.util.concurrent.CountDownLatch
//import kotlin.reflect.KClass
//import org.gradle.bot.GradleBotAppModule
//import org.gradle.bot.client.GitHubClient
//import org.gradle.bot.client.TeamCityClient
//import org.gradle.bot.start
//import org.junit.jupiter.api.extension.AfterAllCallback
//import org.junit.jupiter.api.extension.BeforeAllCallback
//import org.junit.jupiter.api.extension.BeforeEachCallback
//import org.junit.jupiter.api.extension.ExtensionContext
//import org.junit.jupiter.api.extension.TestInstancePostProcessor
//import org.mockito.Mock
//import org.mockito.Mockito
//
//@Target(AnnotationTarget.CLASS)
//@Retention(AnnotationRetention.RUNTIME)
//annotation class VertxGuiceIntegrationTest(val verticleClass: KClass<out Verticle>)
//
//fun <T> Future<T>.await(): T {
//    val countDownLatch = CountDownLatch(1)
//    onComplete {
//        countDownLatch.countDown()
//    }
//
//    countDownLatch.await()
//
//    return result() ?: throw cause()
//}
//
//class TestGradleBotModule(vertx: Vertx) : GradleBotAppModule(vertx) {
//    val mockGitHubClient = Mockito.mock(GitHubClient::class.java)
//    val mockTeamCityClient = Mockito.mock(TeamCityClient::class.java)
//    override fun bindAccessTokens() {
//    }
//
//    override fun configure() {
//        super.configure()
//        Mockito.`when`(mockGitHubClient.init()).thenReturn(Future.succeededFuture("MockUser"))
//        bind(GitHubClient::class.java).toInstance(mockGitHubClient)
//        bind(TeamCityClient::class.java).toInstance(mockTeamCityClient)
//    }
//}
//
//class VertxGuiceIntegrationTestExtension :
//        BeforeAllCallback,
//        AfterAllCallback,
//        TestInstancePostProcessor,
//        BeforeEachCallback {
//
//    lateinit var injector: Injector
//
//    fun ExtensionContext?.getVerticleClass() =
//            this!!.testClass.orElse(null)
//                    ?.getAnnotation(VertxGuiceIntegrationTest::class.java)
//                    ?.verticleClass
//                    ?: throw IllegalStateException("Test class must be annotated with @VertxGuiceIntegrationTest!")
//
//    override fun beforeAll(context: ExtensionContext?) {
//        injector = start(context.getVerticleClass().java, TestGradleBotModule::class.java).await()
//    }
//
//    override fun afterAll(context: ExtensionContext?) {
//        val countDownLatch = CountDownLatch(1)
//        injector.getInstance(Vertx::class.java).undeploy(context.getVerticleClass().java.name).onComplete {
//            countDownLatch.countDown()
//        }
//        countDownLatch.await()
//    }
//
//    override fun postProcessTestInstance(testInstance: Any?, context: ExtensionContext?) {
//        testInstance!!::class.java.declaredFields.forEach {
//            if (it.isAnnotationPresent(Mock::class.java)) {
//                val mockService = injector.getInstance(it.type)
//                it.isAccessible = true
//                it.set(testInstance, mockService)
//            }
//        }
//        injector.injectMembers(testInstance)
//    }
//
//    override fun beforeEach(context: ExtensionContext?) {
//        val testInstance = context!!.testInstance
//        testInstance!!::class.java.declaredFields.forEach {
//            if (it.isAnnotationPresent(Mock::class.java)) {
//                it.isAccessible = true
//                require(Mockito.mockingDetails(it.get(testInstance)).isMock) {
//                    "${it.name} must be a mock instance!"
//                }
//                Mockito.reset(it.get(testInstance))
//            }
//        }
//    }
//}
