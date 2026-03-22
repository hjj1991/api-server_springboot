package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.application.port.input.auth.ProcessQueuedEmailSendRequestsUseCase
import com.hjj.apiserver.application.port.out.auth.ConsumeEmailSendRequestQueuePort
import com.hjj.apiserver.application.port.out.auth.SendAuthEmailPort
import com.hjj.apiserver.application.port.out.auth.model.AuthEmailMessage
import com.hjj.apiserver.application.port.out.auth.model.EmailSendRequest
import mu.two.KotlinLogging
import org.springframework.stereotype.Service

@Service
class AuthEmailDispatchService(
    private val consumeEmailSendRequestQueuePort: ConsumeEmailSendRequestQueuePort,
    private val sendAuthEmailPort: SendAuthEmailPort,
    private val authProperties: AuthProperties,
) : ProcessQueuedEmailSendRequestsUseCase {
    private val log = KotlinLogging.logger {}

    override fun processNextBatch(): Int {
        val queuedRequests =
            consumeEmailSendRequestQueuePort.read(
                batchSize = authProperties.emailWorkerBatchSize,
                visibilityTimeout = authProperties.emailWorkerVisibilityTimeout,
            )

        queuedRequests.forEach { queuedRequest ->
            try {
                val message = toEmailMessage(queuedRequest.request)
                sendAuthEmailPort.send(message)
                consumeEmailSendRequestQueuePort.archive(queuedRequest.messageId)
            } catch (exception: UnsupportedEmailRequestTypeException) {
                log.warn(exception) { "지원하지 않는 이메일 요청 타입이라 메시지를 보관 처리합니다. messageId=${queuedRequest.messageId}" }
                consumeEmailSendRequestQueuePort.archive(queuedRequest.messageId)
            } catch (exception: Exception) {
                log.error(exception) { "이메일 전송 처리에 실패했습니다. messageId=${queuedRequest.messageId}" }
            }
        }

        return queuedRequests.size
    }

    private fun toEmailMessage(request: EmailSendRequest): AuthEmailMessage =
        when (request.type) {
            SIGNUP_VERIFY_EMAIL_TYPE -> {
                val displayName = request.payload["displayName"]?.toString()?.ifBlank { "회원" } ?: "회원"
                val verificationToken = request.payload["verificationToken"]?.toString()
                    ?: throw IllegalArgumentException("verificationToken is required.")
                val expiresAt = request.payload["expiresAt"]?.toString() ?: ""
                val verifyGuide =
                    authProperties.signupVerifyBaseUrl
                        ?.takeIf { it.isNotBlank() }
                        ?.let { "$it?token=$verificationToken" }
                        ?: verificationToken

                AuthEmailMessage(
                    recipientEmail = request.recipientEmail,
                    subject = "[Sundry] 이메일 인증을 완료해 주세요",
                    body =
                        """
                        안녕하세요, ${displayName}님.

                        아래 정보를 사용해 회원가입 이메일 인증을 완료해 주세요.

                        인증 정보: $verifyGuide
                        만료 시각: $expiresAt

                        본인이 요청하지 않았다면 이 메일을 무시해 주세요.
                        """.trimIndent(),
                )
            }

            else -> throw UnsupportedEmailRequestTypeException(request.type)
        }

    private companion object {
        const val SIGNUP_VERIFY_EMAIL_TYPE = "SIGNUP_VERIFY"
    }
}

class UnsupportedEmailRequestTypeException(
    emailType: String,
) : IllegalArgumentException("Unsupported email request type: $emailType")
