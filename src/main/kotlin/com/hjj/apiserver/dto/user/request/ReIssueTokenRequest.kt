package com.hjj.apiserver.dto.user.request

data class ReIssueTokenRequest(
    val type: String,
    val refreshToken: String,
) {
}