package com.hjj.apiserver.dto.user.request

data class UserSignInRequest(
    val userId: String,
    val userPw: String,
)