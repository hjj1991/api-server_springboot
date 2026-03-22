package com.hjj.apiserver.adapter.input.web.auth.request

import com.hjj.apiserver.application.port.input.auth.LocalSignupCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class LocalSignupRequest(
    @field:NotBlank(message = "이름을 입력해주세요.")
    @field:Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
    val displayName: String,
    @field:NotBlank(message = "로그인 아이디를 입력해주세요.")
    @field:Size(min = 4, max = 30, message = "로그인 아이디는 4자 이상 30자 이하로 입력해주세요.")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9._-]{2,28}[a-zA-Z0-9])?$",
        message = "로그인 아이디는 영문, 숫자, ., _, - 만 사용할 수 있습니다.",
    )
    val loginId: String,
    @field:NotBlank(message = "이메일을 입력해주세요.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @field:Size(max = 320, message = "이메일 길이가 너무 깁니다.")
    val email: String,
    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    @field:Size(min = 8, max = 72, message = "비밀번호는 8자 이상 72자 이하로 입력해주세요.")
    val password: String,
) {
    fun toCommand(): LocalSignupCommand =
        LocalSignupCommand(
            displayName = displayName,
            loginId = loginId,
            email = email,
            password = password,
        )
}
