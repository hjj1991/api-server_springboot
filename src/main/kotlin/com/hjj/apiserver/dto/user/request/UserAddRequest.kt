package com.hjj.apiserver.dto.user.request

import com.hjj.apiserver.domain.user.Provider
import javax.validation.constraints.Pattern

class UserAddRequest(
    val userId: String,
    val nickName: String,
    val userEmail: String,
    @Pattern(regexp = "^[a-zA-Z0-9~!@#$%^&*()_+|<>?:{}]{7,14}$", message = "{VALID_CODE0001}")
    val userPw: String,
    val picture: String? = null,
    val provider: Provider? = null,
) {
}