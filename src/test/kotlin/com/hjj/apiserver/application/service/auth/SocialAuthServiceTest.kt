package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.application.port.input.auth.LinkSocialIdentityCommand
import com.hjj.apiserver.application.port.input.auth.ResolveSocialLoginCommand
import com.hjj.apiserver.application.port.input.auth.SocialAccountResolutionType
import com.hjj.apiserver.application.port.input.auth.SocialLoginResolutionResult
import com.hjj.apiserver.application.port.out.auth.CreateSocialUserAccountPort
import com.hjj.apiserver.application.port.out.auth.IssueAccessTokenPort
import com.hjj.apiserver.application.port.out.auth.LinkSocialIdentityPort
import com.hjj.apiserver.application.port.out.auth.LoadAuthenticatedUserByEmailLookupHashPort
import com.hjj.apiserver.application.port.out.auth.LoadAuthenticatedUserPort
import com.hjj.apiserver.application.port.out.auth.LoadSocialAuthenticatedUserPort
import com.hjj.apiserver.application.port.out.auth.RefreshTokenSessionPort
import com.hjj.apiserver.application.port.out.auth.model.AccessTokenIssueCommand
import com.hjj.apiserver.application.port.out.auth.model.CreateSocialUserAccountCommand
import com.hjj.apiserver.application.port.out.auth.model.IssuedAccessToken
import com.hjj.apiserver.application.port.out.auth.model.IssuedRefreshSession
import com.hjj.apiserver.application.port.out.auth.model.LinkSocialIdentityAccountCommand
import com.hjj.apiserver.domain.auth.AuthProviderType
import com.hjj.apiserver.domain.auth.AuthUser
import com.hjj.apiserver.domain.auth.AuthenticatedUserAccount
import com.hjj.apiserver.domain.auth.RoleName
import com.hjj.apiserver.domain.auth.UserStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class SocialAuthServiceTest {
    private lateinit var loadSocialAuthenticatedUserPort: LoadSocialAuthenticatedUserPort
    private lateinit var loadAuthenticatedUserByEmailLookupHashPort: LoadAuthenticatedUserByEmailLookupHashPort
    private lateinit var loadAuthenticatedUserPort: LoadAuthenticatedUserPort
    private lateinit var createSocialUserAccountPort: RecordingCreateSocialUserAccountPort
    private lateinit var linkSocialIdentityPort: RecordingLinkSocialIdentityPort
    private lateinit var issueAccessTokenPort: RecordingIssueAccessTokenPort
    private lateinit var refreshTokenSessionPort: RecordingRefreshTokenSessionPort
    private val fixedClock: Clock = Clock.fixed(Instant.parse("2026-03-22T05:00:00Z"), ZoneOffset.UTC)
    private val authCryptoService = AuthCryptoService("social-auth-secret")
    private val authProperties =
        AuthProperties(
            tokenVersionCacheTtl = Duration.ofHours(12),
            accessTokenTtl = Duration.ofMinutes(10),
            refreshTokenTtl = Duration.ofDays(14),
        )

    private lateinit var socialAuthService: SocialAuthService

    @BeforeEach
    fun setUp() {
        loadSocialAuthenticatedUserPort = Mockito.mock(LoadSocialAuthenticatedUserPort::class.java)
        loadAuthenticatedUserByEmailLookupHashPort = Mockito.mock(LoadAuthenticatedUserByEmailLookupHashPort::class.java)
        loadAuthenticatedUserPort = Mockito.mock(LoadAuthenticatedUserPort::class.java)
        createSocialUserAccountPort = RecordingCreateSocialUserAccountPort()
        linkSocialIdentityPort = RecordingLinkSocialIdentityPort()
        issueAccessTokenPort = RecordingIssueAccessTokenPort()
        refreshTokenSessionPort = RecordingRefreshTokenSessionPort()

        socialAuthService =
            SocialAuthService(
                loadSocialAuthenticatedUserPort = loadSocialAuthenticatedUserPort,
                loadAuthenticatedUserByEmailLookupHashPort = loadAuthenticatedUserByEmailLookupHashPort,
                loadAuthenticatedUserPort = loadAuthenticatedUserPort,
                createSocialUserAccountPort = createSocialUserAccountPort,
                linkSocialIdentityPort = linkSocialIdentityPort,
                issueAccessTokenPort = issueAccessTokenPort,
                refreshTokenSessionPort = refreshTokenSessionPort,
                authCryptoService = authCryptoService,
                authProperties = authProperties,
                clock = fixedClock,
            )
    }

    @Test
    fun `기존 provider subject 가 있으면 기존 사용자로 로그인한다`() {
        val providerSubjectHash = authCryptoService.createProviderSubjectHash(AuthProviderType.GOOGLE, "subject-1")
        Mockito.`when`(
            loadSocialAuthenticatedUserPort.loadByProviderSubjectHash(AuthProviderType.GOOGLE, providerSubjectHash),
        ).thenReturn(activeUserAccount())
        refreshTokenSessionPort.nextIssued =
            IssuedRefreshSession(
                sessionId = "sid-social-1",
                refreshToken = "refresh-social-1",
                expiresAt = OffsetDateTime.parse("2026-04-05T05:00:00Z"),
            )

        val result =
            socialAuthService.resolveLogin(
                ResolveSocialLoginCommand(
                    providerType = AuthProviderType.GOOGLE,
                    providerSubject = "subject-1",
                    displayName = "구글 사용자",
                    email = "hello@example.com",
                    emailVerified = true,
                ),
            )

        assertThat(result).isInstanceOf(SocialLoginResolutionResult.Authenticated::class.java)
        val authenticated = result as SocialLoginResolutionResult.Authenticated
        assertThat(authenticated.userId).isEqualTo(10L)
        assertThat(authenticated.resolutionType).isEqualTo(SocialAccountResolutionType.EXISTING_IDENTITY)
        assertThat(authenticated.authSession.accessToken).startsWith("encoded-10-sid-social-1")
    }

    @Test
    fun `검증된 이메일이 기존 계정과 충돌하면 연결 필요 결과를 반환한다`() {
        val providerSubjectHash = authCryptoService.createProviderSubjectHash(AuthProviderType.KAKAO, "subject-2")
        Mockito.`when`(
            loadSocialAuthenticatedUserPort.loadByProviderSubjectHash(AuthProviderType.KAKAO, providerSubjectHash),
        ).thenReturn(null)
        Mockito.`when`(
            loadAuthenticatedUserByEmailLookupHashPort.loadActiveByEmailLookupHash(
                authCryptoService.createEmailLookupHash("hello@example.com"),
            ),
        ).thenReturn(activeUserAccount())

        val result =
            socialAuthService.resolveLogin(
                ResolveSocialLoginCommand(
                    providerType = AuthProviderType.KAKAO,
                    providerSubject = "subject-2",
                    displayName = "카카오 사용자",
                    email = "hello@example.com",
                    emailVerified = true,
                ),
            )

        assertThat(result).isEqualTo(
            SocialLoginResolutionResult.RequiresLink(
                providerType = AuthProviderType.KAKAO,
                normalizedEmail = "hello@example.com",
            ),
        )
        assertThat(createSocialUserAccountPort.lastCommand).isNull()
    }

    @Test
    fun `기존 identity 와 이메일 충돌이 없으면 새 social 사용자를 생성한다`() {
        val providerSubjectHash = authCryptoService.createProviderSubjectHash(AuthProviderType.NAVER, "subject-3")
        Mockito.`when`(
            loadSocialAuthenticatedUserPort.loadByProviderSubjectHash(AuthProviderType.NAVER, providerSubjectHash),
        ).thenReturn(null)
        Mockito.`when`(
            loadAuthenticatedUserByEmailLookupHashPort.loadActiveByEmailLookupHash(
                authCryptoService.createEmailLookupHash("new@example.com"),
            ),
        ).thenReturn(null)
        createSocialUserAccountPort.nextAccount = activeUserAccount(userId = 31L, tokenVersion = 0L)
        refreshTokenSessionPort.nextIssued =
            IssuedRefreshSession(
                sessionId = "sid-social-2",
                refreshToken = "refresh-social-2",
                expiresAt = OffsetDateTime.parse("2026-04-05T05:00:00Z"),
            )

        val result =
            socialAuthService.resolveLogin(
                ResolveSocialLoginCommand(
                    providerType = AuthProviderType.NAVER,
                    providerSubject = "subject-3",
                    displayName = "네이버 사용자",
                    email = "new@example.com",
                    emailVerified = true,
                ),
            )

        assertThat(result).isInstanceOf(SocialLoginResolutionResult.Authenticated::class.java)
        val authenticated = result as SocialLoginResolutionResult.Authenticated
        assertThat(authenticated.userId).isEqualTo(31L)
        assertThat(authenticated.resolutionType).isEqualTo(SocialAccountResolutionType.CREATED_NEW_USER)
        assertThat(createSocialUserAccountPort.lastCommand?.providerSubjectHash).isEqualTo(providerSubjectHash)
        assertThat(createSocialUserAccountPort.lastCommand?.emailLookupHash)
            .isEqualTo(authCryptoService.createEmailLookupHash("new@example.com"))
    }

    @Test
    fun `로그인된 사용자는 새 SNS identity 를 연결할 수 있다`() {
        val providerSubjectHash = authCryptoService.createProviderSubjectHash(AuthProviderType.APPLE, "subject-4")
        Mockito.`when`(
            loadSocialAuthenticatedUserPort.loadByProviderSubjectHash(AuthProviderType.APPLE, providerSubjectHash),
        ).thenReturn(null)
        Mockito.`when`(loadAuthenticatedUserPort.load(10L)).thenReturn(activeUserAccount())

        val result =
            socialAuthService.linkIdentity(
                LinkSocialIdentityCommand(
                    userId = 10L,
                    providerType = AuthProviderType.APPLE,
                    providerSubject = "subject-4",
                ),
            )

        assertThat(result.userId).isEqualTo(10L)
        assertThat(linkSocialIdentityPort.lastCommand).isEqualTo(
            LinkSocialIdentityAccountCommand(
                userId = 10L,
                providerType = AuthProviderType.APPLE,
                providerSubjectHash = providerSubjectHash,
                linkedAt = OffsetDateTime.parse("2026-03-22T05:00:00Z"),
            ),
        )
    }

    @Test
    fun `이미 다른 사용자에게 연결된 SNS identity 는 다시 연결할 수 없다`() {
        val providerSubjectHash = authCryptoService.createProviderSubjectHash(AuthProviderType.GOOGLE, "subject-5")
        Mockito.`when`(
            loadSocialAuthenticatedUserPort.loadByProviderSubjectHash(AuthProviderType.GOOGLE, providerSubjectHash),
        ).thenReturn(activeUserAccount(userId = 99L))

        assertThatThrownBy {
            socialAuthService.linkIdentity(
                LinkSocialIdentityCommand(
                    userId = 10L,
                    providerType = AuthProviderType.GOOGLE,
                    providerSubject = "subject-5",
                ),
            )
        }.isInstanceOf(com.hjj.apiserver.common.exception.auth.AlreadyLinkedSocialIdentityException::class.java)
    }

    private fun activeUserAccount(
        userId: Long = 10L,
        tokenVersion: Long = 2L,
    ): AuthenticatedUserAccount =
        AuthenticatedUserAccount(
            user =
                AuthUser(
                    userId = userId,
                    displayName = "홍길동",
                    emailCiphertext = authCryptoService.encryptEmail("hello@example.com"),
                    emailLookupHash = authCryptoService.createEmailLookupHash("hello@example.com"),
                    emailVerifiedAt = OffsetDateTime.parse("2026-03-22T04:00:00Z"),
                    tokenVersion = tokenVersion,
                    status = UserStatus.ACTIVE,
                ),
            roleNames = listOf(RoleName.USER),
        )

    private class RecordingCreateSocialUserAccountPort : CreateSocialUserAccountPort {
        var lastCommand: CreateSocialUserAccountCommand? = null
        var nextAccount: AuthenticatedUserAccount? = null

        override fun create(command: CreateSocialUserAccountCommand): AuthenticatedUserAccount {
            lastCommand = command
            return requireNotNull(nextAccount) { "nextAccount must be prepared for social user creation." }
        }
    }

    private class RecordingLinkSocialIdentityPort : LinkSocialIdentityPort {
        var lastCommand: LinkSocialIdentityAccountCommand? = null

        override fun link(command: LinkSocialIdentityAccountCommand): AuthenticatedUserAccount {
            lastCommand = command
            return AuthenticatedUserAccount(
                user =
                    AuthUser(
                        userId = command.userId,
                        displayName = "홍길동",
                        emailCiphertext = "encrypted-email",
                        emailLookupHash = "email-lookup-hash",
                        emailVerifiedAt = command.linkedAt,
                        tokenVersion = 0L,
                        status = UserStatus.ACTIVE,
                    ),
                roleNames = listOf(RoleName.USER),
            )
        }
    }

    private class RecordingIssueAccessTokenPort : IssueAccessTokenPort {
        override fun issue(command: AccessTokenIssueCommand): IssuedAccessToken =
            IssuedAccessToken(
                token = "encoded-${command.userId}-${command.sessionId}",
                jti = "jti-${command.userId}-${command.sessionId}",
                issuedAt = command.issuedAt,
                expiresAt = command.issuedAt.plusMinutes(10),
            )
    }

    private class RecordingRefreshTokenSessionPort : RefreshTokenSessionPort {
        lateinit var nextIssued: IssuedRefreshSession

        override fun issue(
            userId: Long,
            issuedAt: OffsetDateTime,
            expiresAt: OffsetDateTime,
        ): IssuedRefreshSession = nextIssued

        override fun rotate(
            refreshToken: String,
            rotatedAt: OffsetDateTime,
            expiresAt: OffsetDateTime,
        ) = throw UnsupportedOperationException()

        override fun revoke(
            refreshToken: String,
            revokedAt: OffsetDateTime,
        ) = Unit

        override fun revokeAll(
            userId: Long,
            revokedAt: OffsetDateTime,
        ) = Unit
    }
}
