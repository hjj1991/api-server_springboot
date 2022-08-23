package com.hjj.apiserver.dto.user.request

import com.hjj.apiserver.domain.user.Provider
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern

class UserSinUpRequest(
    val userId: String,
    val nickName: @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$", message = "공백제외 한글, 영문, 숫자 2 ~ 10자로 입력해주세요.") String,
    val userEmail: @Email(message = "잘못된 이메일 주소입니다.") String? = null,
    val userPw: @Pattern(regexp = "^[a-zA-Z0-9~!@#$%^&*()_+|<>?:{}]{7,14}$", message = "{VALID_CODE0001}") String,
    val picture: String? = null,
    val provider: Provider? = null,
) {
}