package com.hjj.apiserver.application.port.`in`.user

interface WriteUserUseCase {

    fun signUp(registerUserCommand: RegisterUserCommand)
}