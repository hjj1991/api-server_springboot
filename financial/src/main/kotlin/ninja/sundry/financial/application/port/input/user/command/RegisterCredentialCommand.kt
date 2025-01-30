package com.hjj.apiserver.application.port.input.user.command

import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.User

class RegisterCredentialCommand(
    val userId: String,
    val user: User,
    val userEmail: String? = null,
    val provider: Provider,
)
