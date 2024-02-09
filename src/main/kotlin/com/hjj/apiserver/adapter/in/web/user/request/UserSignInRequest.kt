package com.hjj.apiserver.adapter.`in`.web.user.request

import jakarta.validation.constraints.Pattern

data class UserSignInRequest(
    val userId: String,
    val userPw: @Pattern(regexp = "^[a-zA-Z0-9~!@#$%^&*()_+|<>?:{}]{7,14}$", message = "{VALID_CODE0001}") String,
)