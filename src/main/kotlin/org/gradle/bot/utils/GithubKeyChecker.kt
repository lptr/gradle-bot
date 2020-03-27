package org.gradle.bot.utils

import org.apache.commons.codec.digest.HmacUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GithubKeyChecker {
    private val logger: Logger = LoggerFactory.getLogger(GithubKeyChecker::class.java)

    fun verifySignature(body: String?, signature: String?, secret: String?): Boolean {
        if (logger.isDebugEnabled) {
            logger.debug("signature={} , body={} ", signature, body)
        }

        body ?: return false
        signature ?: return false
        secret ?: return false

        val bodySignature = HmacUtils.hmacSha1Hex(secret, body)
        return "sha1=$bodySignature" == signature

    }

}