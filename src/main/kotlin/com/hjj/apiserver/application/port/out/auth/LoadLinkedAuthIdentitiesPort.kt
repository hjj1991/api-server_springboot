package com.hjj.apiserver.application.port.out.auth

import com.hjj.apiserver.domain.auth.LinkedAuthIdentity

interface LoadLinkedAuthIdentitiesPort {
    fun loadByUserId(userId: Long): List<LinkedAuthIdentity>
}
