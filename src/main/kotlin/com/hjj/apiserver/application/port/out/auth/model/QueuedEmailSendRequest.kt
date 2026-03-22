package com.hjj.apiserver.application.port.out.auth.model

data class QueuedEmailSendRequest(
    val messageId: Long,
    val request: EmailSendRequest,
)
