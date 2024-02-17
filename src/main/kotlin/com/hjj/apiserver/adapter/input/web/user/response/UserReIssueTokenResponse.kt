package com.hjj.apiserver.adapter.input.web.user.response

data class UserReIssueTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
