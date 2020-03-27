package org.gradle.bot.security

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DefaultGithubSignatureCheckerTest {
    private val payload = javaClass.classLoader.getResource("github_payload.json").readText().trim()
    private val githubSignatureChecker = DefaultGitHubSignatureChecker()

    @ParameterizedTest(name = "signature with {0} returns {3}")
    @CsvSource(value = [
        "correct value, sha1=f7ceac8e913abc39bd7e318955c67db290e64f8b, hello,      true",
        "correct value, sha1=a4847957ad54fd147c8a13c9da234b4727165018, gradle-bot, true",
        "fake secret,   sha1=a4847957ad54fd147c8a13c9da234b4727165018, fakeKey,    false",
        "fake sha1,     sha1=iamfakesha1aaaaaaaaaaaaaaaaaaaaaaaaaaaaa, gradle-bot, false",
        "null secret,   sha1=6800e0f1f44f69cdd348360c0140526ff1dff852, ,           false",
        "null signature,                                             , gradle-bot, false"
    ])
    fun testVerifySignature(desc: String, signature: String?, secret: String?, expected: Boolean) {
        assertEquals(expected, githubSignatureChecker.verifySignature(payload, signature, secret))
    }
}