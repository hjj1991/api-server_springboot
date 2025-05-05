package com.hjj.apiserver.application.port.input.user.command

data class CheckUserNickNameDuplicateCommand(
    val userId: Long?,
    val nickName: String,
)
