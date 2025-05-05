package com.hjj.apiserver.adapter.input.web.user.response

data class UserSignInResponse(
    val accessToken: String,
    val refreshToken: String,
)
