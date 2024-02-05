package com.hjj.apiserver.application.port.`in`.user

import com.hjj.apiserver.domain.user.Provider

data class RegisterUserCommand(
    val userId: String,
    val nickName: String,
    val userEmail: String? = null,
    val userPw: String? = null,
    val provider: Provider,
) {

}
