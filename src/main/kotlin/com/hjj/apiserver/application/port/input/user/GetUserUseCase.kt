package com.hjj.apiserver.application.port.input.user

interface GetUserUseCase {
    fun existsUserNickName(command: String)
}
