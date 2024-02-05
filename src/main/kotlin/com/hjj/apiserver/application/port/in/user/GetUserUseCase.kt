package com.hjj.apiserver.application.port.`in`.user

interface GetUserUseCase {
    fun existsNickName(command: CheckUserNickNameDuplicateCommand): Boolean
}