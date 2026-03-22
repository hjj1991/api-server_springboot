package com.hjj.apiserver.adapter.out.mail.auth

import com.hjj.apiserver.application.port.out.auth.SendAuthEmailPort
import com.hjj.apiserver.application.port.out.auth.model.AuthEmailMessage
import com.hjj.apiserver.application.service.auth.AuthProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "app.auth", name = ["email-delivery-mode"], havingValue = "smtp")
class SmtpAuthEmailSenderAdapter(
    private val javaMailSender: JavaMailSender,
    private val authProperties: AuthProperties,
) : SendAuthEmailPort {
    override fun send(message: AuthEmailMessage) {
        javaMailSender.send(
            SimpleMailMessage().apply {
                from = authProperties.emailFromAddress
                setTo(message.recipientEmail)
                subject = message.subject
                text = message.body
            },
        )
    }
}
