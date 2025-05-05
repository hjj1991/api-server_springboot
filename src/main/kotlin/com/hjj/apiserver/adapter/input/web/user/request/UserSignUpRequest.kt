package com.hjj.apiserver.adapter.input.web.user.request

import com.hjj.apiserver.domain.user.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.springframework.security.crypto.password.PasswordEncoder

data class UserSignUpRequest(
    val username: String,
    @Pattern(regexp = "^[a-zA-Z0-9~!@#$%^&*()_+|<>?:{}]{7,14}$", message = "{VALID_CODE0001}")
    val password: String,
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$", message = "공백제외 한글, 영문, 숫자 2 ~ 10자로 입력해주세요.")
    val nickName: String,
    @Email(message = "잘못된 이메일 주소입니다.")
    val userEmail: String? = null,
) {
    fun toUser(passwordEncoder: PasswordEncoder): User =
        User(
            username = username,
            password = passwordEncoder.encode(password),
            nickName = nickName,
            userEmail = userEmail,
        )

}
