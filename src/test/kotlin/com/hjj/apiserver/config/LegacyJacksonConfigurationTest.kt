package com.hjj.apiserver.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.time.LocalDate

class LegacyJacksonConfigurationTest {
    @Test
    fun `legacy object mapper bean serializes kotlin data class with java time`() {
        val context = AnnotationConfigApplicationContext(LegacyJacksonConfiguration::class.java)
        try {
            val objectMapper = context.getBean(ObjectMapper::class.java)

            val json = objectMapper.writeValueAsString(SamplePayload(createdAt = LocalDate.of(2026, 3, 21)))
            val restored = objectMapper.readValue(json, SamplePayload::class.java)

            assertTrue(json.contains("\"createdAt\":\"2026-03-21\""))
            assertEquals(SamplePayload(createdAt = LocalDate.of(2026, 3, 21)), restored)
        } finally {
            context.close()
        }
    }

    private data class SamplePayload(
        val name: String = "sundry",
        val createdAt: LocalDate,
    )
}
