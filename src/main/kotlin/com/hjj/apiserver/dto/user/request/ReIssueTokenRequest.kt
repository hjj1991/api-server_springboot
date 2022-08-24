package com.hjj.apiserver.dto.user.request

class ReIssueTokenRequest(
    val type: String,
    val refreshToken: String,
) {
}