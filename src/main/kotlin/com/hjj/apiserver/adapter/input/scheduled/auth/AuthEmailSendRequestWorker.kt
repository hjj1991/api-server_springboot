package com.hjj.apiserver.adapter.input.scheduled.auth

import com.hjj.apiserver.application.port.input.auth.ProcessQueuedEmailSendRequestsUseCase
import mu.two.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "app.auth", name = ["email-worker-enabled"], havingValue = "true", matchIfMissing = true)
class AuthEmailSendRequestWorker(
    private val processQueuedEmailSendRequestsUseCase: ProcessQueuedEmailSendRequestsUseCase,
) {
    private val log = KotlinLogging.logger {}

    @Scheduled(fixedDelayString = "\${app.auth.email-worker-fixed-delay-millis:15000}")
    fun run() {
        val processedCount = processQueuedEmailSendRequestsUseCase.processNextBatch()
        if (processedCount > 0) {
            log.info { "이메일 발송 큐를 처리했습니다. count=$processedCount" }
        }
    }
}
