package com.hjj.apiserver.application.port.input.user

import com.hjj.apiserver.application.port.input.user.command.RegisterCredentialCommand
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.SnsAccount

interface UserCredentialUseCase {
    fun register(registerCredentialCommand: RegisterCredentialCommand): SnsAccount


    fun isMatchingProvider(provider: Provider): Boolean

    fun existsUserId(userId: String): Boolean
}
