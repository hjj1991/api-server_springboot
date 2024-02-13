package com.hjj.apiserver.application.port.`in`.user

import com.hjj.apiserver.adapter.`in`.web.user.response.UserReIssueTokenResponse
import com.hjj.apiserver.adapter.`in`.web.user.response.UserSignInResponse
import com.hjj.apiserver.application.port.`in`.user.command.RegisterUserCommand
import com.hjj.apiserver.application.port.`in`.user.command.SignInUserCommand

interface WriteUserUseCase {

    fun signUp(registerUserCommand: RegisterUserCommand)
    fun signIn(signInUserCommand: SignInUserCommand): UserSignInResponse

    fun reissueToken(refreshToken: String): UserReIssueTokenResponse
}