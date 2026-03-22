package com.hjj.apiserver.adapter.input.web.auth.request

import com.hjj.apiserver.application.port.input.auth.VerifySignupCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignupVerifyRequest(
    @field:NotBlank(message = "인증 토큰을 입력해주세요.")
    @field:Size(max = 512, message = "인증 토큰 형식이 올바르지 않습니다.")
    val token: String,
) {
    fun toCommand(): VerifySignupCommand = VerifySignupCommand(token = token)
}
