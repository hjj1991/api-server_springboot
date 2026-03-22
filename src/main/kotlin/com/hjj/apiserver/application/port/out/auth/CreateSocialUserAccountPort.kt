package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.application.port.out.auth.model.CreateSocialUserAccountCommand
import com.hjj.apiserver.domain.auth.AuthenticatedUserAccount

interface CreateSocialUserAccountPort {
    fun create(command: CreateSocialUserAccountCommand): AuthenticatedUserAccount
}
