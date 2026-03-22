package com.hjj.apiserver.adapter.input.web.auth.request

import com.hjj.apiserver.application.port.input.auth.LocalLoginCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class LocalLoginRequest(
    @field:NotBlank(message = "로그인 아이디는 필수입니다.")
    @field:Size(min = 4, max = 30, message = "로그인 아이디는 4자 이상 30자 이하여야 합니다.")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9._-]{2,28}[a-zA-Z0-9])?$",
        message = "로그인 아이디는 영문, 숫자, ., _, - 만 사용할 수 있습니다.",
    )
    val loginId: String,
    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(min = 8, max = 255, message = "비밀번호는 8자 이상 255자 이하여야 합니다.")
    val password: String,
) {
    fun toCommand(): LocalLoginCommand =
        LocalLoginCommand(
            loginId = loginId,
            password = password,
        )
}
