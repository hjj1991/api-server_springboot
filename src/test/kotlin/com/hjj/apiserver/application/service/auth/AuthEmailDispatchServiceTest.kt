package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.application.port.out.auth.ConsumeEmailSendRequestQueuePort
import com.hjj.apiserver.application.port.out.auth.SendAuthEmailPort
import com.hjj.apiserver.application.port.out.auth.model.AuthEmailMessage
import com.hjj.apiserver.application.port.out.auth.model.EmailSendRequest
import com.hjj.apiserver.application.port.out.auth.model.QueuedEmailSendRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

class AuthEmailDispatchServiceTest {
    private lateinit var consumeEmailSendRequestQueuePort: RecordingConsumeEmailSendRequestQueuePort
    private lateinit var sendAuthEmailPort: RecordingSendAuthEmailPort
    private lateinit var authEmailDispatchService: AuthEmailDispatchService

    @BeforeEach
    fun setUp() {
        consumeEmailSendRequestQueuePort = RecordingConsumeEmailSendRequestQueuePort()
        sendAuthEmailPort = RecordingSendAuthEmailPort()
        authEmailDispatchService =
            AuthEmailDispatchService(
                consumeEmailSendRequestQueuePort = consumeEmailSendRequestQueuePort,
                sendAuthEmailPort = sendAuthEmailPort,
                authProperties =
                    AuthProperties(
                        emailWorkerBatchSize = 10,
                        emailWorkerVisibilityTimeout = Duration.ofMinutes(5),
                    ),
            )
    }

    @Test
    fun `가입 인증 메일 요청을 읽어 전송 후 archive 한다`() {
        consumeEmailSendRequestQueuePort.nextRequests =
            listOf(
                QueuedEmailSendRequest(
                    messageId = 101L,
                    request =
                        EmailSendRequest(
                            type = "SIGNUP_VERIFY",
                            recipientEmail = "hello@example.com",
                            requestedAt = OffsetDateTime.parse("2026-03-22T03:00:00Z"),
                            payload =
                                mapOf(
                                    "displayName" to "홍길동",
                                    "verificationToken" to "token-123",
                                    "expiresAt" to "2026-03-22T03:30:00Z",
                                ),
                        ),
                ),
            )

        val processedCount = authEmailDispatchService.processNextBatch()

        assertThat(processedCount).isEqualTo(1)
        assertThat(sendAuthEmailPort.sentMessages).hasSize(1)
        assertThat(sendAuthEmailPort.sentMessages.first().recipientEmail).isEqualTo("hello@example.com")
        assertThat(sendAuthEmailPort.sentMessages.first().subject).contains("이메일 인증")
        assertThat(consumeEmailSendRequestQueuePort.archivedMessageIds).containsExactly(101L)
    }

    @Test
    fun `지원하지 않는 메일 타입은 archive 하고 넘어간다`() {
        consumeEmailSendRequestQueuePort.nextRequests =
            listOf(
                QueuedEmailSendRequest(
                    messageId = 202L,
                    request =
                        EmailSendRequest(
                            type = "UNKNOWN",
                            recipientEmail = "hello@example.com",
                            requestedAt = OffsetDateTime.parse("2026-03-22T03:00:00Z"),
                            payload = emptyMap(),
                        ),
                ),
            )

        val processedCount = authEmailDispatchService.processNextBatch()

        assertThat(processedCount).isEqualTo(1)
        assertThat(sendAuthEmailPort.sentMessages).isEmpty()
        assertThat(consumeEmailSendRequestQueuePort.archivedMessageIds).containsExactly(202L)
    }

    @Test
    fun `메일 전송이 실패하면 archive 하지 않는다`() {
        consumeEmailSendRequestQueuePort.nextRequests =
            listOf(
                QueuedEmailSendRequest(
                    messageId = 303L,
                    request =
                        EmailSendRequest(
                            type = "SIGNUP_VERIFY",
                            recipientEmail = "hello@example.com",
                            requestedAt = OffsetDateTime.parse("2026-03-22T03:00:00Z"),
                            payload =
                                mapOf(
                                    "displayName" to "홍길동",
                                    "verificationToken" to "token-123",
                                    "expiresAt" to "2026-03-22T03:30:00Z",
                                ),
                        ),
                ),
            )
        sendAuthEmailPort.throwOnSend = true

        val processedCount = authEmailDispatchService.processNextBatch()

        assertThat(processedCount).isEqualTo(1)
        assertThat(consumeEmailSendRequestQueuePort.archivedMessageIds).isEmpty()
    }

    private class RecordingConsumeEmailSendRequestQueuePort : ConsumeEmailSendRequestQueuePort {
        var nextRequests: List<QueuedEmailSendRequest> = emptyList()
        val archivedMessageIds: MutableList<Long> = mutableListOf()

        override fun read(
            batchSize: Int,
            visibilityTimeout: Duration,
        ): List<QueuedEmailSendRequest> = nextRequests

        override fun archive(messageId: Long) {
            archivedMessageIds += messageId
        }
    }

    private class RecordingSendAuthEmailPort : SendAuthEmailPort {
        val sentMessages: MutableList<AuthEmailMessage> = mutableListOf()
        var throwOnSend: Boolean = false

        override fun send(message: AuthEmailMessage) {
            if (throwOnSend) {
                throw IllegalStateException("send failed")
            }
            sentMessages += message
        }
    }
}
