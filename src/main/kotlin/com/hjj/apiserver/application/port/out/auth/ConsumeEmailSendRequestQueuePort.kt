package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.application.port.out.auth.model.QueuedEmailSendRequest
import java.time.Duration

interface ConsumeEmailSendRequestQueuePort {
    fun read(
        batchSize: Int,
        visibilityTimeout: Duration,
    ): List<QueuedEmailSendRequest>

    fun archive(messageId: Long)
}
