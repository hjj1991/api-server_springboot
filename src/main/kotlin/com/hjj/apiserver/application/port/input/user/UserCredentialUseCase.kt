package com.hjj.apiserver.application.port.input.user

import com.hjj.apiserver.application.port.input.user.command.RegisterCredentialCommand
import com.hjj.apiserver.domain.user.Credential
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.dto.user.UserAttribute

interface UserCredentialUseCase {
    fun register(registerCredentialCommand: RegisterCredentialCommand): Credential

    fun signIn(userAttribute: UserAttribute): Credential

    fun isMatchingProvider(provider: Provider): Boolean

    fun existsUserId(userId: String): Boolean
}
