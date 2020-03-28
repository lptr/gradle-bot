package org.gradle.bot.model

import org.gradle.bot.objectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class WhoAmIResponseTest {
    @Test
    fun `WhoAmIResponse without name works`() {
        val whoAmIResponse = objectMapper.readValue("""
            {"data":{"viewer":{"login":"bot-gradle","name":null}}}
            """, WhoAmIResponse::class.java)
        Assertions.assertEquals("bot-gradle", whoAmIResponse.data.viewer.login)
        Assertions.assertNull(whoAmIResponse.data.viewer.name)
    }
}