package com.hjj.apiserver.application.port.`in`.user.command

import com.hjj.apiserver.domain.user.Provider

data class SignInUserCommand(
    val userId: String,
    val userPw: String? = null,
    val provider: Provider,
) {
}