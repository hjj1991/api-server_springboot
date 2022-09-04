package com.hjj.apiserver.dto.user.request

data class ReIssueTokenRequest(
    val refreshToken: String,
) {
}