package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.domain.auth.AuthenticatedUserAccount

interface LoadAuthenticatedUserByEmailLookupHashPort {
    fun loadActiveByEmailLookupHash(emailLookupHash: String): AuthenticatedUserAccount?
}
