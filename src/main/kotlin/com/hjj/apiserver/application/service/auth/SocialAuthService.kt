package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.application.port.input.auth.AuthSessionResult
import com.hjj.apiserver.application.port.input.auth.LinkSocialIdentityCommand
import com.hjj.apiserver.application.port.input.auth.ManageSocialAuthUseCase
import com.hjj.apiserver.application.port.input.auth.ResolveSocialLoginCommand
import com.hjj.apiserver.application.port.input.auth.SocialAccountResolutionType
import com.hjj.apiserver.application.port.input.auth.SocialIdentityLinkResult
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
import com.hjj.apiserver.application.port.out.auth.model.LinkSocialIdentityAccountCommand
import com.hjj.apiserver.common.ErrConst
import com.hjj.apiserver.common.exception.BaseException
import com.hjj.apiserver.common.exception.NotFoundException
import com.hjj.apiserver.common.exception.auth.AlreadyLinkedSocialIdentityException
import com.hjj.apiserver.domain.auth.AuthProviderType
import com.hjj.apiserver.domain.auth.AuthenticatedUserAccount
import com.hjj.apiserver.domain.auth.RoleName
import com.hjj.apiserver.domain.auth.UserStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.OffsetDateTime

@Service
class SocialAuthService(
    private val loadSocialAuthenticatedUserPort: LoadSocialAuthenticatedUserPort,
    private val loadAuthenticatedUserByEmailLookupHashPort: LoadAuthenticatedUserByEmailLookupHashPort,
    private val loadAuthenticatedUserPort: LoadAuthenticatedUserPort,
    private val createSocialUserAccountPort: CreateSocialUserAccountPort,
    private val linkSocialIdentityPort: LinkSocialIdentityPort,
    private val issueAccessTokenPort: IssueAccessTokenPort,
    private val refreshTokenSessionPort: RefreshTokenSessionPort,
    private val authCryptoService: AuthCryptoService,
    private val authProperties: AuthProperties,
    private val clock: Clock,
) : ManageSocialAuthUseCase {
    @Transactional
    override fun resolveLogin(command: ResolveSocialLoginCommand): SocialLoginResolutionResult {
        requireSocialProvider(command.providerType)

        val now = OffsetDateTime.now(clock)
        val providerSubjectHash = authCryptoService.createProviderSubjectHash(command.providerType, command.providerSubject)

        loadSocialAuthenticatedUserPort.loadByProviderSubjectHash(command.providerType, providerSubjectHash)?.let { account ->
            ensureActiveUser(account)
            return SocialLoginResolutionResult.Authenticated(
                authSession = issueAuthSession(account, now),
                userId = account.user.userId,
                resolutionType = SocialAccountResolutionType.EXISTING_IDENTITY,
            )
        }

        val normalizedVerifiedEmail = normalizeVerifiedEmail(command.email, command.emailVerified)
        if (normalizedVerifiedEmail != null) {
            val emailLookupHash = authCryptoService.createEmailLookupHash(normalizedVerifiedEmail)
            val existingUser = loadAuthenticatedUserByEmailLookupHashPort.loadActiveByEmailLookupHash(emailLookupHash)
            if (existingUser != null) {
                return SocialLoginResolutionResult.RequiresLink(
                    providerType = command.providerType,
                    normalizedEmail = normalizedVerifiedEmail,
                )
            }
        }

        val createdAccount =
            createSocialUserAccountPort.create(
                CreateSocialUserAccountCommand(
                    providerType = command.providerType,
                    providerSubjectHash = providerSubjectHash,
                    displayName = resolveDisplayName(command.displayName, command.providerType),
                    encryptedEmail = normalizedVerifiedEmail?.let(authCryptoService::encryptEmail),
                    emailLookupHash = normalizedVerifiedEmail?.let(authCryptoService::createEmailLookupHash),
                    emailVerifiedAt = normalizedVerifiedEmail?.let { now },
                    linkedAt = now,
                ),
            )

        return SocialLoginResolutionResult.Authenticated(
            authSession = issueAuthSession(createdAccount, now),
            userId = createdAccount.user.userId,
            resolutionType = SocialAccountResolutionType.CREATED_NEW_USER,
        )
    }

    @Transactional
    override fun linkIdentity(command: LinkSocialIdentityCommand): SocialIdentityLinkResult {
        requireSocialProvider(command.providerType)

        val now = OffsetDateTime.now(clock)
        val providerSubjectHash = authCryptoService.createProviderSubjectHash(command.providerType, command.providerSubject)

        loadSocialAuthenticatedUserPort.loadByProviderSubjectHash(command.providerType, providerSubjectHash)?.let { account ->
            if (account.user.userId != command.userId) {
                throw AlreadyLinkedSocialIdentityException()
            }

            return SocialIdentityLinkResult(
                userId = command.userId,
                providerType = command.providerType,
                linkedAt = now,
            )
        }

        val currentUser =
            loadAuthenticatedUserPort.load(command.userId)
                ?: throw NotFoundException(ErrConst.ERR_CODE0001)

        ensureActiveUser(currentUser)

        linkSocialIdentityPort.link(
            LinkSocialIdentityAccountCommand(
                userId = command.userId,
                providerType = command.providerType,
                providerSubjectHash = providerSubjectHash,
                linkedAt = now,
            ),
        )

        return SocialIdentityLinkResult(
            userId = command.userId,
            providerType = command.providerType,
            linkedAt = now,
        )
    }

    private fun issueAuthSession(
        account: AuthenticatedUserAccount,
        issuedAt: OffsetDateTime,
    ): AuthSessionResult {
        val roles = requireRoleNames(account.roleNames)
        val refreshSession =
            refreshTokenSessionPort.issue(
                userId = account.user.userId,
                issuedAt = issuedAt,
                expiresAt = issuedAt.plus(authProperties.refreshTokenTtl),
            )
        val issuedAccessToken =
            issueAccessTokenPort.issue(
                AccessTokenIssueCommand(
                    userId = account.user.userId,
                    tokenVersion = account.user.tokenVersion,
                    sessionId = refreshSession.sessionId,
                    roles = roles,
                    issuedAt = issuedAt,
                ),
            )

        return AuthSessionResult(
            accessToken = issuedAccessToken.token,
            accessTokenExpiresAt = issuedAccessToken.expiresAt,
            refreshToken = refreshSession.refreshToken,
            refreshTokenExpiresAt = refreshSession.expiresAt,
        )
    }

    private fun normalizeVerifiedEmail(
        email: String?,
        emailVerified: Boolean,
    ): String? =
        email
            ?.takeIf { emailVerified }
            ?.takeIf { it.isNotBlank() }
            ?.let(authCryptoService::normalizeEmail)

    private fun resolveDisplayName(
        displayName: String?,
        providerType: AuthProviderType,
    ): String = displayName?.trim().takeUnless { it.isNullOrBlank() } ?: "${providerType.name.lowercase()} 사용자"

    private fun ensureActiveUser(account: AuthenticatedUserAccount) {
        if (account.user.status != UserStatus.ACTIVE) {
            throw BaseException(ErrConst.ERR_CODE0009, "접근 권한이 없습니다.")
        }
    }

    private fun requireRoleNames(roleNames: List<RoleName>): List<String> {
        val roles = roleNames.map { it.name }.distinct().sorted()
        if (roles.isEmpty()) {
            throw BaseException(ErrConst.ERR_CODE0015, ErrConst.ERR_CODE0015.msg)
        }
        return roles
    }

    private fun requireSocialProvider(providerType: AuthProviderType) {
        if (providerType == AuthProviderType.LOCAL) {
            throw BaseException(ErrConst.ERR_CODE0016, "LOCAL provider 는 SNS 로그인 흐름에 사용할 수 없습니다.")
        }
    }
}
