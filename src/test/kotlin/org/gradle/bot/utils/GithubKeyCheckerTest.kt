package org.gradle.bot.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


class GithubKeyCheckerTest {

    @ParameterizedTest
    @CsvSource(value = [
        // test for normal
        "sha1=f7ceac8e913abc39bd7e318955c67db290e64f8b,hello,true",
        "sha1=a4847957ad54fd147c8a13c9da234b4727165018,gradle-bot,true",
        // test for fake secret
        "sha1=a4847957ad54fd147c8a13c9da234b4727165018,fakeKey,false",
        //test for fake sha1
        "sha1=iamfakesha1aaaaaaaaaaaaaaaaaaaaaaaaaaaaa,gradle-bot,false",
        // test for null secret
        "sha1=6800e0f1f44f69cdd348360c0140526ff1dff852,,false",
        // test for null signature
        ",gradle-bot,false"
    ]
    )
    fun testVerifySignature(signature: String?, secret: String?, expected: Boolean) {
        val payload = this.javaClass.classLoader.getResource("github_payload.json").readText().trim()
        assertEquals(expected, GithubKeyChecker.verifySignature(payload, signature, secret))
    }
}