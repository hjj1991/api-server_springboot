package com.hjj.apiserver.adapter.out.queue.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.application.port.out.auth.ConsumeEmailSendRequestQueuePort
import com.hjj.apiserver.application.port.out.auth.model.EmailSendRequest
import com.hjj.apiserver.application.port.out.auth.model.QueuedEmailSendRequest
import com.hjj.apiserver.application.service.auth.AuthProperties
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.atomic.AtomicBoolean

@Component
class PgmqEmailSendRequestQueueAdapter(
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
    private val authProperties: AuthProperties,
) : ConsumeEmailSendRequestQueuePort {
    private val queueInitialized = AtomicBoolean(false)

    override fun read(
        batchSize: Int,
        visibilityTimeout: Duration,
    ): List<QueuedEmailSendRequest> {
        initializeQueueIfNeeded()

        return namedParameterJdbcTemplate.query(
            """
            SELECT msg_id, message
            FROM pgmq.read(:queueName::text, :visibilityTimeoutSeconds::integer, :batchSize::integer)
            """.trimIndent(),
            MapSqlParameterSource()
                .addValue("queueName", authProperties.emailSendQueueName)
                .addValue("visibilityTimeoutSeconds", visibilityTimeout.seconds.toInt())
                .addValue("batchSize", batchSize),
        ) { rs, _ ->
            QueuedEmailSendRequest(
                messageId = rs.getLong("msg_id"),
                request = objectMapper.readValue(rs.getString("message"), EmailSendRequest::class.java),
            )
        }
    }

    override fun archive(messageId: Long) {
        initializeQueueIfNeeded()
        namedParameterJdbcTemplate.queryForObject(
            "SELECT pgmq.archive(:queueName::text, :messageId::bigint)",
            MapSqlParameterSource()
                .addValue("queueName", authProperties.emailSendQueueName)
                .addValue("messageId", messageId),
            Boolean::class.java,
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
