package com.hjj.apiserver.application.port.`in`.user

import com.hjj.apiserver.application.port.`in`.user.command.CheckUserNickNameDuplicateCommand

interface GetUserUseCase {
    fun existsNickName(command: CheckUserNickNameDuplicateCommand): Boolean
}