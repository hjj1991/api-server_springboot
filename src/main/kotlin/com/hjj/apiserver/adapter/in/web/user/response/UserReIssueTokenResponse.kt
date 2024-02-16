package com.hjj.apiserver.adapter.`in`.web.user.response

data class UserReIssueTokenResponse(
    val accessToken: String,
    val refreshToken: String,
)
