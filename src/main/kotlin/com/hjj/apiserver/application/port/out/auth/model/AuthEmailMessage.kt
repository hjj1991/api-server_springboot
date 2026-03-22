package com.hjj.apiserver.application.port.out.auth.model

data class AuthEmailMessage(
    val recipientEmail: String,
    val subject: String,
    val body: String,
)
