package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.application.port.input.auth.LocalSignupCommand
import com.hjj.apiserver.application.port.input.auth.RegisterLocalSignupUseCase
import com.hjj.apiserver.application.port.input.auth.SignupAcceptedResult
import com.hjj.apiserver.application.port.input.auth.SignupVerifiedResult
import com.hjj.apiserver.application.port.input.auth.VerifySignupCommand
import com.hjj.apiserver.application.port.out.auth.CheckDuplicatedEmailPort
import com.hjj.apiserver.application.port.out.auth.CheckDuplicatedLoginIdPort
import com.hjj.apiserver.application.port.out.auth.CreateLocalUserAccountPort
import com.hjj.apiserver.application.port.out.auth.EnqueueEmailSendRequestPort
import com.hjj.apiserver.application.port.out.auth.PendingSignupPort
import com.hjj.apiserver.application.port.out.auth.model.CreateLocalUserAccountCommand
import com.hjj.apiserver.application.port.out.auth.model.EmailSendRequest
import com.hjj.apiserver.application.port.out.auth.model.PendingSignup
import com.hjj.apiserver.common.exception.auth.DuplicatedSignupEmailException
import com.hjj.apiserver.common.exception.auth.DuplicatedSignupLoginIdException
import com.hjj.apiserver.common.exception.auth.InvalidSignupVerificationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.OffsetDateTime

@Service
class LocalSignupService(
    private val checkDuplicatedEmailPort: CheckDuplicatedEmailPort,
    private val checkDuplicatedLoginIdPort: CheckDuplicatedLoginIdPort,
    private val createLocalUserAccountPort: CreateLocalUserAccountPort,
    private val pendingSignupPort: PendingSignupPort,
    private val enqueueEmailSendRequestPort: EnqueueEmailSendRequestPort,
    private val passwordEncoder: PasswordEncoder,
    private val clock: Clock,
    private val authCryptoService: AuthCryptoService,
    private val authProperties: AuthProperties,
) : RegisterLocalSignupUseCase {
    @Transactional
    override fun signup(command: LocalSignupCommand): SignupAcceptedResult {
        val normalizedLoginId = authCryptoService.normalizeLoginId(command.loginId)
        val normalizedEmail = authCryptoService.normalizeEmail(command.email)
        val emailLookupHash = authCryptoService.createEmailLookupHash(normalizedEmail)

        if (checkDuplicatedLoginIdPort.existsByLoginId(normalizedLoginId)) {
            throw DuplicatedSignupLoginIdException()
        }

        if (checkDuplicatedEmailPort.existsByEmailLookupHash(emailLookupHash)) {
            throw DuplicatedSignupEmailException()
        }

        val requestedAt = OffsetDateTime.now(clock)
        val expiresAt = requestedAt.plus(authProperties.signupPendingTtl)
        val verificationToken = authCryptoService.createVerificationToken()
        val passwordHash = requireNotNull(passwordEncoder.encode(command.password)) { "Password hash must not be null." }

        pendingSignupPort.save(
            verificationToken = verificationToken,
            signup =
                PendingSignup(
                    displayName = command.displayName.trim(),
                    normalizedLoginId = normalizedLoginId,
                    normalizedEmail = normalizedEmail,
                    emailLookupHash = emailLookupHash,
                    passwordHash = passwordHash,
                    requestedAt = requestedAt,
                    expiresAt = expiresAt,
                ),
        )

        enqueueEmailSendRequestPort.enqueue(
            EmailSendRequest(
                type = SIGNUP_VERIFY_EMAIL_TYPE,
                recipientEmail = normalizedEmail,
                requestedAt = requestedAt,
                payload =
                    mapOf(
                        "displayName" to command.displayName.trim(),
                        "verificationToken" to verificationToken,
                        "expiresAt" to expiresAt.toString(),
                    ),
            ),
        )

        return SignupAcceptedResult(
            email = normalizedEmail,
            expiresAt = expiresAt,
        )
    }

    @Transactional
    override fun verifySignup(command: VerifySignupCommand): SignupVerifiedResult {
        val now = OffsetDateTime.now(clock)
        val pendingSignup =
            pendingSignupPort.findByVerificationToken(command.token)
                ?: throw InvalidSignupVerificationException()

        if (pendingSignup.expiresAt.isBefore(now)) {
            pendingSignupPort.deleteByVerificationToken(command.token)
            throw InvalidSignupVerificationException()
        }

        if (checkDuplicatedEmailPort.existsByEmailLookupHash(pendingSignup.emailLookupHash)) {
            pendingSignupPort.deleteByVerificationToken(command.token)
            throw DuplicatedSignupEmailException()
        }

        createLocalUserAccountPort.create(
            CreateLocalUserAccountCommand(
                displayName = pendingSignup.displayName,
                loginId = pendingSignup.normalizedLoginId,
                encryptedEmail = authCryptoService.encryptEmail(pendingSignup.normalizedEmail),
                emailLookupHash = pendingSignup.emailLookupHash,
                emailVerifiedAt = now,
                providerSubjectHash = authCryptoService.createLocalProviderSubjectHash(),
                passwordHash = pendingSignup.passwordHash,
                passwordAlgo = extractPasswordAlgorithm(pendingSignup.passwordHash),
                passwordUpdatedAt = now,
            ),
        )

        pendingSignupPort.deleteByVerificationToken(command.token)

        return SignupVerifiedResult(
            email = pendingSignup.normalizedEmail,
            verifiedAt = now,
        )
    }

    private fun extractPasswordAlgorithm(passwordHash: String): String =
        passwordHash.substringAfter('{', "").substringBefore('}', "").ifBlank { "unknown" }

    private companion object {
        const val SIGNUP_VERIFY_EMAIL_TYPE = "SIGNUP_VERIFY"
    }
}
