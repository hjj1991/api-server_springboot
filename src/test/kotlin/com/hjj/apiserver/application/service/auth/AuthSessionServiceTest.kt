package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.application.port.input.auth.GetCurrentUserQuery
import com.hjj.apiserver.application.port.input.auth.LocalLoginCommand
import com.hjj.apiserver.application.port.input.auth.LogoutAllSessionsCommand
import com.hjj.apiserver.application.port.input.auth.LogoutSessionCommand
import com.hjj.apiserver.application.port.input.auth.ManageUserTokenVersionUseCase
import com.hjj.apiserver.application.port.input.auth.RefreshSessionCommand
import com.hjj.apiserver.application.port.out.auth.IssueAccessTokenPort
import com.hjj.apiserver.application.port.out.auth.LoadAuthenticatedUserPort
import com.hjj.apiserver.application.port.out.auth.LoadLocalLoginAccountPort
import com.hjj.apiserver.application.port.out.auth.RecordLocalLoginSuccessPort
import com.hjj.apiserver.application.port.out.auth.RefreshTokenSessionPort
import com.hjj.apiserver.application.port.out.auth.model.AccessTokenIssueCommand
import com.hjj.apiserver.application.port.out.auth.model.IssuedAccessToken
import com.hjj.apiserver.application.port.out.auth.model.IssuedRefreshSession
import com.hjj.apiserver.application.port.out.auth.model.RefreshTokenRejectionReason
import com.hjj.apiserver.application.port.out.auth.model.RefreshTokenRotationResult
import com.hjj.apiserver.domain.auth.AuthIdentityStatus
import com.hjj.apiserver.domain.auth.AuthUser
import com.hjj.apiserver.domain.auth.AuthenticatedUserAccount
import com.hjj.apiserver.domain.auth.LocalLoginAccount
import com.hjj.apiserver.domain.auth.RoleName
import com.hjj.apiserver.domain.auth.UserStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class AuthSessionServiceTest {
    private lateinit var loadLocalLoginAccountPort: LoadLocalLoginAccountPort
    private lateinit var recordLocalLoginSuccessPort: RecordLocalLoginSuccessPort
    private lateinit var loadAuthenticatedUserPort: LoadAuthenticatedUserPort
    private lateinit var refreshTokenSessionPort: RecordingRefreshTokenSessionPort
    private lateinit var accessTokenDenylistPort: RecordingAccessTokenDenylistPort
    private lateinit var manageUserTokenVersionUseCase: ManageUserTokenVersionUseCase
    private lateinit var issueAccessTokenPort: RecordingIssueAccessTokenPort
    private val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
    private val fixedClock: Clock = Clock.fixed(Instant.parse("2026-03-22T03:00:00Z"), ZoneOffset.UTC)
    private val authCryptoService = AuthCryptoService("auth-secret")
    private val authProperties =
        AuthProperties(
            tokenVersionCacheTtl = Duration.ofHours(12),
            accessTokenTtl = Duration.ofMinutes(10),
            refreshTokenTtl = Duration.ofDays(14),
        )

    private lateinit var authSessionService: AuthSessionService

    @BeforeEach
    fun setUp() {
        loadLocalLoginAccountPort = Mockito.mock(LoadLocalLoginAccountPort::class.java)
        recordLocalLoginSuccessPort = Mockito.mock(RecordLocalLoginSuccessPort::class.java)
        loadAuthenticatedUserPort = Mockito.mock(LoadAuthenticatedUserPort::class.java)
        refreshTokenSessionPort = RecordingRefreshTokenSessionPort()
        accessTokenDenylistPort = RecordingAccessTokenDenylistPort()
        manageUserTokenVersionUseCase = Mockito.mock(ManageUserTokenVersionUseCase::class.java)
        issueAccessTokenPort = RecordingIssueAccessTokenPort()

        authSessionService =
            AuthSessionService(
                loadLocalLoginAccountPort = loadLocalLoginAccountPort,
                recordLocalLoginSuccessPort = recordLocalLoginSuccessPort,
                loadAuthenticatedUserPort = loadAuthenticatedUserPort,
                passwordEncoder = passwordEncoder,
                issueAccessTokenPort = issueAccessTokenPort,
                refreshTokenSessionPort = refreshTokenSessionPort,
                accessTokenDenylistPort = accessTokenDenylistPort,
                manageUserTokenVersionUseCase = manageUserTokenVersionUseCase,
                authCryptoService = authCryptoService,
                authProperties = authProperties,
                clock = fixedClock,
            )
    }

    @Test
    fun `로그인 성공시 access 와 refresh 세션을 발급한다`() {
        val user = activeUser()
        val localLoginAccount =
            LocalLoginAccount(
                user = user,
                identityStatus = AuthIdentityStatus.ACTIVE,
                passwordHash = requireNotNull(passwordEncoder.encode("password123")),
                lockedUntil = null,
                roleNames = listOf(RoleName.USER),
            )

        Mockito.`when`(loadLocalLoginAccountPort.loadByLoginId(authCryptoService.normalizeLoginId("hello-user")))
            .thenReturn(localLoginAccount)
        refreshTokenSessionPort.nextIssued =
            IssuedRefreshSession(
                sessionId = "sid-1",
                refreshToken = "refresh-token-1",
                expiresAt = OffsetDateTime.parse("2026-04-05T03:00:00Z"),
            )

        val result =
            authSessionService.login(
                LocalLoginCommand(
                    loginId = "hello-user",
                    password = "password123",
                ),
            )

        assertThat(result.accessToken).startsWith("encoded-10-sid-1")
        assertThat(result.accessTokenExpiresAt).isEqualTo(OffsetDateTime.parse("2026-03-22T03:10:00Z"))
        assertThat(result.refreshToken).isEqualTo("refresh-token-1")
        Mockito.verify(recordLocalLoginSuccessPort).record(10L, OffsetDateTime.parse("2026-03-22T03:00:00Z"))
    }

    @Test
    fun `리프레시 성공시 새 access 와 refresh 세션을 발급한다`() {
        val user = activeUser(tokenVersion = 3L)
        Mockito.`when`(loadAuthenticatedUserPort.load(user.userId)).thenReturn(
            AuthenticatedUserAccount(
                user = user,
                roleNames = listOf(RoleName.USER, RoleName.ADMIN),
            ),
        )
        refreshTokenSessionPort.nextRotation =
            RefreshTokenRotationResult.Rotated(
                sessionId = "sid-2",
                userId = user.userId,
                refreshToken = "refresh-token-2",
                expiresAt = OffsetDateTime.parse("2026-04-05T03:00:00Z"),
            )

        val result =
            authSessionService.refresh(
                RefreshSessionCommand(
                    refreshToken = "refresh-token-1",
                ),
            )

        assertThat(result.accessToken).startsWith("encoded-10-sid-2")
        assertThat(result.refreshToken).isEqualTo("refresh-token-2")
        assertThat(result.accessTokenExpiresAt).isEqualTo(OffsetDateTime.parse("2026-03-22T03:10:00Z"))
    }

    @Test
    fun `로그아웃시 access 토큰을 denylist 에 추가하고 refresh 세션을 revoke 한다`() {
        authSessionService.logout(
            LogoutSessionCommand(
                userId = 10L,
                accessJti = "access-jti",
                accessExpiresAt = OffsetDateTime.parse("2026-03-22T03:10:00Z"),
                refreshToken = "refresh-token-1",
            ),
        )

        assertThat(accessTokenDenylistPort.deniedJti).isEqualTo("access-jti")
        assertThat(refreshTokenSessionPort.revokedRefreshToken).isEqualTo("refresh-token-1")
    }

    @Test
    fun `모든 기기 로그아웃시 token version 증가와 전체 refresh 세션 revoke 를 수행한다`() {
        Mockito.`when`(manageUserTokenVersionUseCase.increaseTokenVersion(10L)).thenReturn(3L)

        authSessionService.logoutAll(
            LogoutAllSessionsCommand(
                userId = 10L,
                accessJti = "access-jti",
                accessExpiresAt = OffsetDateTime.parse("2026-03-22T03:10:00Z"),
            ),
        )

        Mockito.verify(manageUserTokenVersionUseCase).increaseTokenVersion(10L)
        assertThat(refreshTokenSessionPort.revokedAllUserId).isEqualTo(10L)
        assertThat(accessTokenDenylistPort.deniedJti).isEqualTo("access-jti")
    }

    @Test
    fun `리프레시 토큰 재사용 감지시 전체 refresh 세션을 정리하고 실패한다`() {
        refreshTokenSessionPort.nextRotation =
            RefreshTokenRotationResult.Rejected(
                reason = RefreshTokenRejectionReason.REUSED,
                userId = 10L,
            )

        org.assertj.core.api.Assertions.assertThatThrownBy {
            authSessionService.refresh(
                RefreshSessionCommand(
                    refreshToken = "reused-refresh-token",
                ),
            )
        }.isInstanceOf(com.hjj.apiserver.common.exception.auth.InvalidRefreshSessionException::class.java)

        assertThat(refreshTokenSessionPort.revokedAllUserId).isEqualTo(10L)
    }

    @Test
    fun `내 정보 조회시 복호화된 이메일과 역할을 반환한다`() {
        val user = activeUser()
        Mockito.`when`(loadAuthenticatedUserPort.load(user.userId)).thenReturn(
            AuthenticatedUserAccount(
                user = user,
                roleNames = listOf(RoleName.USER),
            ),
        )

        val result = authSessionService.getCurrentUser(GetCurrentUserQuery(user.userId))

        assertThat(result.displayName).isEqualTo("홍길동")
        assertThat(result.email).isEqualTo("hello@example.com")
        assertThat(result.roles).containsExactly("USER")
    }

    private fun activeUser(tokenVersion: Long = 2L): AuthUser =
        AuthUser(
            userId = 10L,
            displayName = "홍길동",
            emailCiphertext = authCryptoService.encryptEmail("hello@example.com"),
            emailLookupHash = authCryptoService.createEmailLookupHash("hello@example.com"),
            emailVerifiedAt = OffsetDateTime.parse("2026-03-22T02:00:00Z"),
            tokenVersion = tokenVersion,
            status = UserStatus.ACTIVE,
        )

    private class RecordingRefreshTokenSessionPort : RefreshTokenSessionPort {
        lateinit var nextIssued: IssuedRefreshSession
        lateinit var nextRotation: RefreshTokenRotationResult
        var revokedRefreshToken: String? = null
        var revokedAllUserId: Long? = null

        override fun issue(
            userId: Long,
            issuedAt: OffsetDateTime,
            expiresAt: OffsetDateTime,
        ): IssuedRefreshSession = nextIssued

        override fun rotate(
            refreshToken: String,
            rotatedAt: OffsetDateTime,
            expiresAt: OffsetDateTime,
        ): RefreshTokenRotationResult = nextRotation

        override fun revoke(
            refreshToken: String,
            revokedAt: OffsetDateTime,
        ) {
            revokedRefreshToken = refreshToken
        }

        override fun revokeAll(
            userId: Long,
            revokedAt: OffsetDateTime,
        ) {
            revokedAllUserId = userId
        }
    }

    private class RecordingIssueAccessTokenPort : IssueAccessTokenPort {
        override fun issue(command: AccessTokenIssueCommand): IssuedAccessToken =
            IssuedAccessToken(
                token = "encoded-${command.userId}-${command.sessionId}",
                jti = "jti-${command.sessionId}",
                issuedAt = command.issuedAt,
                expiresAt = command.issuedAt.plusMinutes(10),
            )
    }

    private class RecordingAccessTokenDenylistPort : com.hjj.apiserver.application.port.out.auth.AccessTokenDenylistPort {
        var deniedJti: String? = null

        override fun isDenied(jti: String): Boolean = deniedJti == jti

        override fun deny(
            jti: String,
            expiresAt: Instant,
        ) {
            deniedJti = jti
        }
    }
}
