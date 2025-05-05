package com.hjj.apiserver.application.service.user

import com.hjj.apiserver.adapter.input.web.user.request.UserSignInRequest
import com.hjj.apiserver.adapter.input.web.user.request.UserSignUpRequest
import com.hjj.apiserver.adapter.input.web.user.response.UserReIssueTokenResponse
import com.hjj.apiserver.adapter.input.web.user.response.UserSignInResponse
import com.hjj.apiserver.application.port.input.user.GetUserUseCase
import com.hjj.apiserver.application.port.input.user.WriteUserUseCase
import com.hjj.apiserver.application.port.out.user.GetRolePort
import com.hjj.apiserver.application.port.out.user.GetUserPort
import com.hjj.apiserver.application.port.out.user.GetUserRolePort
import com.hjj.apiserver.application.port.out.user.WriteRefreshTokenPort
import com.hjj.apiserver.application.port.out.user.WriteUserLogPort
import com.hjj.apiserver.application.port.out.user.WriteUserPort
import com.hjj.apiserver.application.port.out.user.WriteUserRolePort
import com.hjj.apiserver.common.JwtProvider
import com.hjj.apiserver.common.exception.DuplicatedNickNameException
import com.hjj.apiserver.common.exception.UserNotFoundException
import com.hjj.apiserver.domain.user.LogType
import com.hjj.apiserver.domain.user.RefreshToken
import com.hjj.apiserver.domain.user.RoleType
import com.hjj.apiserver.domain.user.UserLog
import com.hjj.apiserver.domain.user.UserRole
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val writeUserPort: WriteUserPort,
    private val writeUserRolePort: WriteUserRolePort,
    private val writeUserLogPort: WriteUserLogPort,
    private val writeRefreshTokenPort: WriteRefreshTokenPort,
    private val getUserPort: GetUserPort,
    private val getRolePort: GetRolePort,
    private val getUserRolePort: GetUserRolePort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
) : WriteUserUseCase, GetUserUseCase {

    @Transactional
    override fun signUp(userSignUpRequest: UserSignUpRequest) {
        if (getUserPort.existsUserNickName(userSignUpRequest.nickName)) {
            throw DuplicatedNickNameException()
        }

        val user = writeUserPort.insertUser(userSignUpRequest.toUser(passwordEncoder))
        val role = getRolePort.findByRoleType(RoleType.USER)
        writeUserRolePort.insertUserRole(
            UserRole(
                userId = user.id,
                roleId = role.id,
            )
        )

        writeUserLogPort.insertUserLog(
            UserLog(
                logType = LogType.SIGNUP,
                userId = user.id
            )
        )
    }

    @Transactional
    override fun signIn(userSignInRequest: UserSignInRequest, userAgent: String?): UserSignInResponse {
        val user = getUserPort.findByUsername(userSignInRequest.username)
        if (!passwordEncoder.matches(userSignInRequest.password, user.password)) {
            throw UserNotFoundException()
        }
        writeRefreshTokenPort.revokeAllTokensByUserId(user.id)
        val roles = getUserRolePort.findRolesByUserId(user.id)
        val accessToken = jwtProvider.createAccessToken(
            userId = user.id,
            roles = roles
        )

        val refreshToken = jwtProvider.createRefreshToken(
            userId = user.id,
        )

        writeRefreshTokenPort.insertRefreshToken(
            RefreshToken(
                jti = refreshToken.id,
                userId = user.id,
                issuedAt = refreshToken.issuedAt,
                expiresAt = refreshToken.expiresAt,
                revoked = false,
                userAgent = userAgent,
            )
        )

        return UserSignInResponse(
            accessToken = accessToken.tokenValue,
            refreshToken = refreshToken.tokenValue
        )
    }

    override fun reissueToken(refreshToken: String): UserReIssueTokenResponse {
        TODO("Not yet implemented")
    }

    override fun existsUserNickName(nickName: String) {
        if (getUserPort.existsUserNickName(nickName)) {
            throw DuplicatedNickNameException()
        }
    }

}
