package com.hjj.apiserver.application.port.out.auth

interface CheckDuplicatedEmailPort {
    fun existsByEmailLookupHash(emailLookupHash: String): Boolean
}
