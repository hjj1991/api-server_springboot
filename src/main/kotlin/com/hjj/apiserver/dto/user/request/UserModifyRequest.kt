package com.hjj.apiserver.dto.user.request

import javax.validation.constraints.Pattern

class UserModifyRequest(
    val nickName: String,
    val userEmail: String,
    @Pattern(regexp = "^[a-zA-Z0-9~!@#$%^&*()_+|<>?:{}]{7,14}$", message = "비밀번호는 영문 숫자 조합 7 ~ 14자리 이상입니다.")
    val userPw: String,
) {
}