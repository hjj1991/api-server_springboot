package com.hjj.apiserver.application.port.`in`.user

import com.hjj.apiserver.common.component.SelfValidating
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.user.CurrentUserInfo
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CheckUserNickNameDuplicateCommand(
    val authUser: User,
    @field:NotBlank
    val nickName: String
)