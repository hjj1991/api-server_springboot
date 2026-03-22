package com.hjj.apiserver.adapter.input.web.auth.request

data class LogoutRequest(
    val refreshToken: String? = null,
)
