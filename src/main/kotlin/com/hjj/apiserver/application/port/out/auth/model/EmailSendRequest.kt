package com.hjj.apiserver.application.port.out.auth.model

import java.time.OffsetDateTime

data class EmailSendRequest(
    val type: String,
    val recipientEmail: String,
    val requestedAt: OffsetDateTime,
    val payload: Map<String, Any>,
)
