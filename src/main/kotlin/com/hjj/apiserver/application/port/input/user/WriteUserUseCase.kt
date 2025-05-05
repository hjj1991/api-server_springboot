package com.hjj.apiserver.application.port.input.user

import com.hjj.apiserver.adapter.input.web.user.request.UserSignInRequest
import com.hjj.apiserver.adapter.input.web.user.request.UserSignUpRequest
import com.hjj.apiserver.adapter.input.web.user.response.UserReIssueTokenResponse
import com.hjj.apiserver.adapter.input.web.user.response.UserSignInResponse

interface WriteUserUseCase {
    fun signUp(userSignUpRequest: UserSignUpRequest)

    fun signIn(userSignInRequest: UserSignInRequest, userAgent: String?): UserSignInResponse

    fun reissueToken(refreshToken: String): UserReIssueTokenResponse
}
