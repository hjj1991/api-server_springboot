package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.application.port.out.auth.model.LinkSocialIdentityAccountCommand
import com.hjj.apiserver.domain.auth.AuthenticatedUserAccount

interface LinkSocialIdentityPort {
    fun link(command: LinkSocialIdentityAccountCommand): AuthenticatedUserAccount
}
