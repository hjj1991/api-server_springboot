package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.domain.auth.AuthenticatedUserAccount

interface LoadAuthenticatedUserPort {
    fun load(userId: Long): AuthenticatedUserAccount?
}
