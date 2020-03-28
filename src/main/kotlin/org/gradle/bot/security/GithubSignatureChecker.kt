package org.gradle.bot.security

import org.apache.commons.codec.digest.HmacUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Named
import javax.inject.Singleton

interface GithubSignatureChecker {
    fun verifySignature(body: String?, signature: String?): Boolean
}

@Singleton
class DefaultGitHubSignatureChecker(
        @Named("GITHUB_WEBHOOK_SECRET") private val secret: String
) : GithubSignatureChecker {
    override fun verifySignature(body: String?, signature: String?): Boolean {
        body ?: return false
        signature ?: return false

        return HmacUtils.hmacSha1Hex(secret, body).let { "sha1=$it" == signature }
    }
}