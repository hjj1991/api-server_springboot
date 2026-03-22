package com.hjj.apiserver.adapter.out.mail.auth

import com.hjj.apiserver.application.port.out.auth.SendAuthEmailPort
import com.hjj.apiserver.application.port.out.auth.model.AuthEmailMessage
import mu.two.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "app.auth", name = ["email-delivery-mode"], havingValue = "log", matchIfMissing = true)
class LoggingAuthEmailSenderAdapter : SendAuthEmailPort {
    private val log = KotlinLogging.logger {}

    override fun send(message: AuthEmailMessage) {
        log.info {
            """
            [AUTH_EMAIL_LOG]
            to=${message.recipientEmail}
            subject=${message.subject}
            body=
            ${message.body}
            """.trimIndent()
        }
    }
}
