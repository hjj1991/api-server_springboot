package com.hjj.apiserver.application.service.auth

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("app.auth")
class AuthProperties(
    var signupPendingTtl: Duration = Duration.ofMinutes(30),
    var emailSendQueueName: String = "email_send_requests",
    var tokenVersionCacheTtl: Duration = Duration.ofHours(12),
    var accessTokenTtl: Duration = Duration.ofMinutes(10),
    var refreshTokenTtl: Duration = Duration.ofDays(14),
    var emailWorkerBatchSize: Int = 20,
    var emailWorkerVisibilityTimeout: Duration = Duration.ofMinutes(5),
    var emailWorkerFixedDelayMillis: Long = 15_000,
    var emailDeliveryMode: String = "log",
    var emailFromAddress: String = "no-reply@sundry.local",
    var signupVerifyBaseUrl: String? = null,
)
