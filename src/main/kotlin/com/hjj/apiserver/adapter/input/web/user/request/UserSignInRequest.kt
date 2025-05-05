package com.hjj.apiserver.adapter.input.web.user.request

import jakarta.validation.constraints.Pattern

data class UserSignInRequest(
    val username: String,
    @Pattern(regexp = "^[a-zA-Z0-9~!@#$%^&*()_+|<>?:{}]{7,14}$", message = "{VALID_CODE0001}")
    val password: String,
)
