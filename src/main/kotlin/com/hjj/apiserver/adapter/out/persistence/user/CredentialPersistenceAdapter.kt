package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.application.port.out.user.GetCredentialPort
import com.hjj.apiserver.application.port.out.user.WriteCredentialPort
import com.hjj.apiserver.common.PersistenceAdapter
import com.hjj.apiserver.domain.user.Credential
import com.hjj.apiserver.domain.user.Provider

@PersistenceAdapter
class CredentialPersistenceAdapter(
    private val credentialRepository: CredentialRepository,
    private val credentialMapper: CredentialMapper,
    private val userRepository: UserRepository,
): WriteCredentialPort, GetCredentialPort {
    override fun registerCredential(credential: Credential): Credential {
        val credentialEntity = credentialRepository.save(
            CredentialEntity(
                credentialNo = credential.credentialNo,
                userId = credential.userId,
                credentialEmail = credential.credentialEmail,
                provider = credential.provider,
                userEntity = userRepository.getReferenceById(credential.user.userNo),
            )
        )
        return credentialMapper.mapToDomainEntity(credentialEntity)
    }

    override fun findExistsCredentialByUserIdAndProvider(userId: String, provider: Provider): Boolean {
        return credentialRepository.findCredentialUserIdByUserIdAndProvider(userId, provider)
    }
}