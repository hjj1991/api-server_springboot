package com.hjj.apiserver.adapter.`in`.web.user.request

import jakarta.validation.constraints.NotBlank

data class ReIssueTokenRequest(
    @field:NotBlank
    val refreshToken: String,
)
