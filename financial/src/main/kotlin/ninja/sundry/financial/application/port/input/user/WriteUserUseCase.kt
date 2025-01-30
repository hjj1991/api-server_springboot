package com.hjj.apiserver.application.port.input.user

import com.hjj.apiserver.adapter.input.web.user.response.UserReIssueTokenResponse
import com.hjj.apiserver.adapter.input.web.user.response.UserSignInResponse
import com.hjj.apiserver.application.port.input.user.command.RegisterUserCommand
import com.hjj.apiserver.application.port.input.user.command.SignInUserCommand

interface WriteUserUseCase {
    fun signUp(registerUserCommand: RegisterUserCommand)

    fun signIn(signInUserCommand: SignInUserCommand): UserSignInResponse

    fun reissueToken(refreshToken: String): UserReIssueTokenResponse
}
