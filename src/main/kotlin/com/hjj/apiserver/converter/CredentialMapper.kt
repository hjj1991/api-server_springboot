package com.hjj.apiserver.converter

import com.hjj.apiserver.adapter.out.persistence.user.CredentialEntity
import com.hjj.apiserver.domain.user.Credential
import org.springframework.stereotype.Component

@Component
class CredentialMapper(
    private val userMapper: UserMapper,
) {
    fun mapToDomainEntity(credentialEntity: CredentialEntity): Credential {
        return Credential(
            credentialNo = credentialEntity.credentialNo,
            userId = credentialEntity.userId,
            credentialEmail = credentialEntity.credentialEmail,
            provider = credentialEntity.provider,
            user = userMapper.mapToDomainEntity(credentialEntity.userEntity),
            state = credentialEntity.state,
        )
    }
}
