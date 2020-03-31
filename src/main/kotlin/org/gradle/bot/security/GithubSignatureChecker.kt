package org.gradle.bot.security

import com.google.inject.Inject
import org.apache.commons.codec.digest.HmacUtils
import javax.inject.Named
import javax.inject.Singleton

interface GithubSignatureChecker {
    fun verifySignature(body: String?, signature: String?): Boolean
}

enum class LenientGitHubSignatureCheck:GithubSignatureChecker {
    INSTANCE {
        override fun verifySignature(body: String?, signature: String?) = true
    };
}

@Singleton
class Sha1GitHubSignatureChecker @Inject constructor(
        @Named("GITHUB_WEBHOOK_SECRET") private val secret: String
) : GithubSignatureChecker {
    override fun verifySignature(body: String?, signature: String?): Boolean {
        body ?: return false
        signature ?: return false

        return HmacUtils.hmacSha1Hex(secret, body).let { "sha1=$it" == signature }
    }
}