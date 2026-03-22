package com.hjj.apiserver.adapter.out.queue.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.application.port.out.auth.EnqueueEmailSendRequestPort
import com.hjj.apiserver.application.port.out.auth.model.EmailSendRequest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

@Component
class PgmqEmailSendRequestAdapter(
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
    private val authProperties: com.hjj.apiserver.application.service.auth.AuthProperties,
) : EnqueueEmailSendRequestPort {
    private val queueInitialized = AtomicBoolean(false)

    override fun enqueue(request: EmailSendRequest) {
        initializeQueueIfNeeded()

        namedParameterJdbcTemplate.queryForObject(
            "SELECT * FROM pgmq.send(:queueName::text, CAST(:payload AS jsonb))",
            MapSqlParameterSource()
                .addValue("queueName", authProperties.emailSendQueueName)
                .addValue(
                    "payload",
                    objectMapper.writeValueAsString(
                        mapOf(
                            "type" to request.type,
                            "recipientEmail" to request.recipientEmail,
                            "requestedAt" to request.requestedAt,
                            "payload" to request.payload,
                        ),
                    ),
                ),
            Long::class.java,
        )
    }

    private fun initializeQueueIfNeeded() {
        if (queueInitialized.compareAndSet(false, true)) {
            namedParameterJdbcTemplate.jdbcTemplate.execute(
                "SELECT pgmq.create('${authProperties.emailSendQueueName}')",
            )
        }
    }
}
