package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.application.port.input.auth.LocalSignupCommand
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
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class LocalSignupServiceTest {
    private lateinit var checkDuplicatedEmailPort: CheckDuplicatedEmailPort
    private lateinit var checkDuplicatedLoginIdPort: CheckDuplicatedLoginIdPort
    private lateinit var createLocalUserAccountPort: RecordingCreateLocalUserAccountPort
    private lateinit var pendingSignupPort: RecordingPendingSignupPort
    private lateinit var enqueueEmailSendRequestPort: RecordingEmailSendRequestPort
    private lateinit var passwordEncoder: PasswordEncoder
    private val fixedClock: Clock = Clock.fixed(Instant.parse("2026-03-22T00:00:00Z"), ZoneOffset.UTC)
    private val authCryptoService = AuthCryptoService("signup-secret")
    private val authProperties = AuthProperties(Duration.ofMinutes(30), "email_send_requests")

    private lateinit var localSignupService: LocalSignupService

    @BeforeEach
    fun setUp() {
        checkDuplicatedEmailPort = Mockito.mock(CheckDuplicatedEmailPort::class.java)
        checkDuplicatedLoginIdPort = Mockito.mock(CheckDuplicatedLoginIdPort::class.java)
        createLocalUserAccountPort = RecordingCreateLocalUserAccountPort()
        pendingSignupPort = RecordingPendingSignupPort()
        enqueueEmailSendRequestPort = RecordingEmailSendRequestPort()
        passwordEncoder = Mockito.mock(PasswordEncoder::class.java)

        localSignupService =
            LocalSignupService(
                checkDuplicatedEmailPort = checkDuplicatedEmailPort,
                checkDuplicatedLoginIdPort = checkDuplicatedLoginIdPort,
                createLocalUserAccountPort = createLocalUserAccountPort,
                pendingSignupPort = pendingSignupPort,
                enqueueEmailSendRequestPort = enqueueEmailSendRequestPort,
                passwordEncoder = passwordEncoder,
                clock = fixedClock,
                authCryptoService = authCryptoService,
                authProperties = authProperties,
            )
    }

    @Test
    fun `이미 가입된 이메일이면 가입 요청을 거절한다`() {
        val emailLookupHash = authCryptoService.createEmailLookupHash("hello@example.com")
        Mockito.`when`(checkDuplicatedLoginIdPort.existsByLoginId("hello-user")).thenReturn(false)
        Mockito.`when`(checkDuplicatedEmailPort.existsByEmailLookupHash(emailLookupHash)).thenReturn(true)

        assertThatThrownBy {
            localSignupService.signup(
                LocalSignupCommand(
                    displayName = "홍길동",
                    loginId = "hello-user",
                    email = "hello@example.com",
                    password = "password123",
                ),
            )
        }.isInstanceOf(DuplicatedSignupEmailException::class.java)

        assertThat(pendingSignupPort.savedToken).isNull()
        assertThat(enqueueEmailSendRequestPort.lastRequest).isNull()
    }

    @Test
    fun `이미 가입된 로그인 아이디면 가입 요청을 거절한다`() {
        Mockito.`when`(checkDuplicatedLoginIdPort.existsByLoginId("hello-user")).thenReturn(true)

        assertThatThrownBy {
            localSignupService.signup(
                LocalSignupCommand(
                    displayName = "홍길동",
                    loginId = "hello-user",
                    email = "hello@example.com",
                    password = "password123",
                ),
            )
        }.isInstanceOf(DuplicatedSignupLoginIdException::class.java)

        assertThat(pendingSignupPort.savedToken).isNull()
        assertThat(enqueueEmailSendRequestPort.lastRequest).isNull()
    }

    @Test
    fun `가입 요청 성공시 pending signup 을 저장하고 이메일 발송을 enqueue 한다`() {
        val emailLookupHash = authCryptoService.createEmailLookupHash("hello@example.com")
        Mockito.`when`(checkDuplicatedLoginIdPort.existsByLoginId("hello-user")).thenReturn(false)
        Mockito.`when`(checkDuplicatedEmailPort.existsByEmailLookupHash(emailLookupHash)).thenReturn(false)
        Mockito.`when`(passwordEncoder.encode("password123")).thenReturn("{bcrypt}encoded-password")

        val result =
            localSignupService.signup(
                LocalSignupCommand(
                    displayName = "홍길동",
                    loginId = "hello-user",
                    email = "hello@example.com",
                    password = "password123",
                ),
            )

        assertThat(result.email).isEqualTo("hello@example.com")
        assertThat(result.expiresAt).isEqualTo(OffsetDateTime.parse("2026-03-22T00:30:00Z"))
        assertThat(pendingSignupPort.savedToken).isNotBlank()
        assertThat(pendingSignupPort.savedSignup?.normalizedLoginId).isEqualTo("hello-user")
        assertThat(pendingSignupPort.savedSignup?.emailLookupHash).isEqualTo(emailLookupHash)
        assertThat(pendingSignupPort.savedSignup?.passwordHash).isEqualTo("{bcrypt}encoded-password")
        assertThat(enqueueEmailSendRequestPort.lastRequest?.type).isEqualTo("SIGNUP_VERIFY")
        assertThat(enqueueEmailSendRequestPort.lastRequest?.recipientEmail).isEqualTo("hello@example.com")
        assertThat(enqueueEmailSendRequestPort.lastRequest?.payload?.get("verificationToken")).isEqualTo(pendingSignupPort.savedToken)
    }

    @Test
    fun `가입 인증 성공시 로컬 계정 생성 포트를 호출한다`() {
        val pendingSignup =
            PendingSignup(
                displayName = "홍길동",
                normalizedLoginId = "hello-user",
                normalizedEmail = "hello@example.com",
                emailLookupHash = authCryptoService.createEmailLookupHash("hello@example.com"),
                passwordHash = "{bcrypt}encoded-password",
                requestedAt = OffsetDateTime.parse("2026-03-22T00:00:00Z"),
                expiresAt = OffsetDateTime.parse("2026-03-22T00:30:00Z"),
            )

        pendingSignupPort.pendingSignups["verification-token"] = pendingSignup
        Mockito.`when`(checkDuplicatedEmailPort.existsByEmailLookupHash(pendingSignup.emailLookupHash)).thenReturn(false)

        val result = localSignupService.verifySignup(VerifySignupCommand("verification-token"))

        assertThat(result.email).isEqualTo("hello@example.com")
        assertThat(result.verifiedAt).isEqualTo(OffsetDateTime.parse("2026-03-22T00:00:00Z"))

        assertThat(createLocalUserAccountPort.lastCommand).isNotNull
        assertThat(createLocalUserAccountPort.lastCommand?.displayName).isEqualTo("홍길동")
        assertThat(createLocalUserAccountPort.lastCommand?.loginId).isEqualTo("hello-user")
        assertThat(createLocalUserAccountPort.lastCommand?.emailLookupHash).isEqualTo(pendingSignup.emailLookupHash)
        assertThat(createLocalUserAccountPort.lastCommand?.passwordHash).isEqualTo("{bcrypt}encoded-password")
        assertThat(pendingSignupPort.deletedToken).isEqualTo("verification-token")
    }

    @Test
    fun `가입 인증 토큰이 없으면 실패한다`() {
        assertThatThrownBy {
            localSignupService.verifySignup(VerifySignupCommand("missing-token"))
        }.isInstanceOf(InvalidSignupVerificationException::class.java)
    }

    private class RecordingPendingSignupPort : PendingSignupPort {
        val pendingSignups: MutableMap<String, PendingSignup> = mutableMapOf()
        var savedToken: String? = null
        var savedSignup: PendingSignup? = null
        var deletedToken: String? = null

        override fun save(
            verificationToken: String,
            signup: PendingSignup,
        ) {
            savedToken = verificationToken
            savedSignup = signup
            pendingSignups[verificationToken] = signup
        }

        override fun findByVerificationToken(token: String): PendingSignup? = pendingSignups[token]

        override fun deleteByVerificationToken(token: String) {
            deletedToken = token
            pendingSignups.remove(token)
        }
    }

    private class RecordingEmailSendRequestPort : EnqueueEmailSendRequestPort {
        var lastRequest: EmailSendRequest? = null

        override fun enqueue(request: EmailSendRequest) {
            lastRequest = request
        }
    }

    private class RecordingCreateLocalUserAccountPort : CreateLocalUserAccountPort {
        var lastCommand: CreateLocalUserAccountCommand? = null

        override fun create(command: CreateLocalUserAccountCommand) {
            lastCommand = command
        }
    }
}
