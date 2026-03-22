package com.hjj.apiserver.application.service.auth

import com.hjj.apiserver.application.port.input.auth.AuthSessionResult
import com.hjj.apiserver.application.port.input.auth.AuthenticatedUserResult
import com.hjj.apiserver.application.port.input.auth.GetCurrentUserQuery
import com.hjj.apiserver.application.port.input.auth.LocalLoginCommand
import com.hjj.apiserver.application.port.input.auth.LogoutAllSessionsCommand
import com.hjj.apiserver.application.port.input.auth.LogoutSessionCommand
import com.hjj.apiserver.application.port.input.auth.ManageAuthSessionUseCase
import com.hjj.apiserver.application.port.input.auth.ManageUserTokenVersionUseCase
import com.hjj.apiserver.application.port.input.auth.RefreshSessionCommand
import com.hjj.apiserver.application.port.out.auth.AccessTokenDenylistPort
import com.hjj.apiserver.application.port.out.auth.IssueAccessTokenPort
import com.hjj.apiserver.application.port.out.auth.LoadAuthenticatedUserPort
import com.hjj.apiserver.application.port.out.auth.LoadLocalLoginAccountPort
import com.hjj.apiserver.application.port.out.auth.RecordLocalLoginSuccessPort
import com.hjj.apiserver.application.port.out.auth.RefreshTokenSessionPort
import com.hjj.apiserver.application.port.out.auth.model.AccessTokenIssueCommand
import com.hjj.apiserver.application.port.out.auth.model.RefreshTokenRotationResult
import com.hjj.apiserver.common.ErrConst
import com.hjj.apiserver.common.exception.BaseException
import com.hjj.apiserver.common.exception.auth.InvalidRefreshSessionException
import com.hjj.apiserver.domain.auth.AuthUser
import com.hjj.apiserver.domain.auth.LocalLoginAccount
import com.hjj.apiserver.domain.auth.RoleName
import com.hjj.apiserver.domain.auth.UserStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.OffsetDateTime

@Service
class AuthSessionService(
    private val loadLocalLoginAccountPort: LoadLocalLoginAccountPort,
    private val recordLocalLoginSuccessPort: RecordLocalLoginSuccessPort,
    private val loadAuthenticatedUserPort: LoadAuthenticatedUserPort,
    private val passwordEncoder: PasswordEncoder,
    private val issueAccessTokenPort: IssueAccessTokenPort,
    private val refreshTokenSessionPort: RefreshTokenSessionPort,
    private val accessTokenDenylistPort: AccessTokenDenylistPort,
    private val manageUserTokenVersionUseCase: ManageUserTokenVersionUseCase,
    private val authCryptoService: AuthCryptoService,
    private val authProperties: AuthProperties,
    private val clock: Clock,
) : ManageAuthSessionUseCase {
    @Transactional
    override fun login(command: LocalLoginCommand): AuthSessionResult {
        val now = OffsetDateTime.now(clock)
        val loginAccount = findLoginAccount(command)

        if (!loginAccount.isLoginAllowedAt(now) || !passwordEncoder.matches(command.password, loginAccount.passwordHash)) {
            throw BadCredentialsException(ErrConst.ERR_CODE0008.msg)
        }

        recordLocalLoginSuccessPort.record(loginAccount.user.userId, now)

        return issueAuthSession(
            user = loginAccount.user,
            roleNames = loginAccount.roleNames,
            issuedAt = now,
        )
    }

    @Transactional(readOnly = true)
    override fun refresh(command: RefreshSessionCommand): AuthSessionResult {
        val now = OffsetDateTime.now(clock)
        return when (
            val rotationResult =
                refreshTokenSessionPort.rotate(
                    refreshToken = command.refreshToken,
                    rotatedAt = now,
                    expiresAt = now.plus(authProperties.refreshTokenTtl),
                )
        ) {
            is RefreshTokenRotationResult.Rotated -> {
                val authenticatedUser =
                    loadAuthenticatedUserPort.load(rotationResult.userId)
                        ?: throw InvalidRefreshSessionException()

                if (authenticatedUser.user.status != UserStatus.ACTIVE) {
                    refreshTokenSessionPort.revokeAll(authenticatedUser.user.userId, now)
                    throw InvalidRefreshSessionException()
                }

                val issuedAccessToken =
                    issueAccessTokenPort.issue(
                        AccessTokenIssueCommand(
                            userId = authenticatedUser.user.userId,
                            tokenVersion = authenticatedUser.user.tokenVersion,
                            sessionId = rotationResult.sessionId,
                            roles = requireRoleNames(authenticatedUser.roleNames),
                            issuedAt = now,
                        ),
                    )

                AuthSessionResult(
                    accessToken = issuedAccessToken.token,
                    accessTokenExpiresAt = issuedAccessToken.expiresAt,
                    refreshToken = rotationResult.refreshToken,
                    refreshTokenExpiresAt = rotationResult.expiresAt,
                )
            }

            is RefreshTokenRotationResult.Rejected -> {
                rotationResult.userId?.let { refreshTokenSessionPort.revokeAll(it, now) }
                throw InvalidRefreshSessionException()
            }
        }
    }

    override fun logout(command: LogoutSessionCommand) {
        accessTokenDenylistPort.deny(command.accessJti, command.accessExpiresAt.toInstant())
        command.refreshToken?.takeIf { it.isNotBlank() }?.let { refreshTokenSessionPort.revoke(it, OffsetDateTime.now(clock)) }
    }

    override fun logoutAll(command: LogoutAllSessionsCommand) {
        val now = OffsetDateTime.now(clock)
        manageUserTokenVersionUseCase.increaseTokenVersion(command.userId)
        refreshTokenSessionPort.revokeAll(command.userId, now)
        accessTokenDenylistPort.deny(command.accessJti, command.accessExpiresAt.toInstant())
    }

    @Transactional(readOnly = true)
    override fun getCurrentUser(query: GetCurrentUserQuery): AuthenticatedUserResult {
        val user =
            loadAuthenticatedUserPort.load(query.userId)
                ?: throw InvalidRefreshSessionException()

        ensureActiveSessionUser(user.user)

        return AuthenticatedUserResult(
            id = user.user.userId,
            displayName = user.user.displayName,
            email = user.user.emailCiphertext?.let(authCryptoService::decryptEmail),
            roles = requireRoleNames(user.roleNames),
        )
    }

    private fun issueAuthSession(
        user: AuthUser,
        roleNames: List<RoleName>,
        issuedAt: OffsetDateTime,
    ): AuthSessionResult {
        val roles = requireRoleNames(roleNames)
        val refreshSession =
            refreshTokenSessionPort.issue(
                userId = user.userId,
                issuedAt = issuedAt,
                expiresAt = issuedAt.plus(authProperties.refreshTokenTtl),
            )
        val issuedAccessToken =
            issueAccessTokenPort.issue(
                AccessTokenIssueCommand(
                    userId = user.userId,
                    tokenVersion = user.tokenVersion,
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

    private fun findLoginAccount(command: LocalLoginCommand): LocalLoginAccount {
        val normalizedLoginId = authCryptoService.normalizeLoginId(command.loginId)
        return loadLocalLoginAccountPort.loadByLoginId(normalizedLoginId)
            ?: throw BadCredentialsException(ErrConst.ERR_CODE0008.msg)
    }

    private fun ensureActiveSessionUser(user: AuthUser) {
        if (user.status != UserStatus.ACTIVE) {
            throw InvalidRefreshSessionException()
        }
    }

    private fun requireRoleNames(roleNames: List<RoleName>): List<String> {
        val roles = roleNames.map { it.name }.distinct().sorted()
        if (roles.isEmpty()) {
            throw BaseException(ErrConst.ERR_CODE0015, ErrConst.ERR_CODE0015.msg)
        }
        return roles
    }
}
