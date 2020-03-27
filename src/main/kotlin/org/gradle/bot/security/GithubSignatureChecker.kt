package org.gradle.bot.security

import org.apache.commons.codec.digest.HmacUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

interface GithubSignatureChecker {
    fun verifySignature(body: String?, signature: String?, secret: String?): Boolean
}

@Singleton
class DefaultGitHubSignatureChecker : GithubSignatureChecker {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun verifySignature(body: String?, signature: String?, secret: String?): Boolean {
        body ?: return false
        signature ?: return false
        secret ?: return false

        return HmacUtils.hmacSha1Hex(secret, body).let { "sha1=$it" == signature }
    }

}