package com.hjj.apiserver.adapter.input.web.user.request

import jakarta.validation.constraints.NotBlank

data class ReIssueTokenRequest(
    @field:NotBlank
    val refreshToken: String,
)
