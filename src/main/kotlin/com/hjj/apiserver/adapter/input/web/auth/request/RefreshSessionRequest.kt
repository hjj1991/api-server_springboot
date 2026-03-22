package com.hjj.apiserver.adapter.input.web.auth.request

import com.hjj.apiserver.application.port.input.auth.RefreshSessionCommand
import jakarta.validation.constraints.NotBlank

data class RefreshSessionRequest(
    @field:NotBlank(message = "refreshToken 은 필수입니다.")
    val refreshToken: String,
) {
    fun toCommand(): RefreshSessionCommand = RefreshSessionCommand(refreshToken = refreshToken)
}
