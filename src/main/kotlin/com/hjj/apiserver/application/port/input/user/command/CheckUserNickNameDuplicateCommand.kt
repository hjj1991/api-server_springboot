package com.hjj.apiserver.application.port.input.user.command

import com.hjj.apiserver.domain.user.User
import jakarta.validation.constraints.NotBlank

data class CheckUserNickNameDuplicateCommand(
    val authUser: User,
    @field:NotBlank
    val nickName: String,
)
