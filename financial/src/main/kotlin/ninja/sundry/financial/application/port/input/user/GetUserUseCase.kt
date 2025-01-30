package com.hjj.apiserver.application.port.input.user

import com.hjj.apiserver.application.port.input.user.command.CheckUserNickNameDuplicateCommand

interface GetUserUseCase {
    fun existsNickName(command: CheckUserNickNameDuplicateCommand): Boolean
}
