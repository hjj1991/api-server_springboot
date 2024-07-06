package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.adapter.out.persistence.user.entity.CredentialEntity
import com.hjj.apiserver.adapter.out.persistence.user.repository.CredentialRepository
import com.hjj.apiserver.adapter.out.persistence.user.repository.UserRepository
import com.hjj.apiserver.application.port.out.user.GetCredentialPort
import com.hjj.apiserver.application.port.out.user.WriteCredentialPort
import com.hjj.apiserver.common.PersistenceAdapter
import com.hjj.apiserver.converter.CredentialMapper
import com.hjj.apiserver.domain.user.Credential
import com.hjj.apiserver.domain.user.Provider

@PersistenceAdapter
class CredentialPersistenceAdapter(
    private val credentialRepository: CredentialRepository,
    private val credentialMapper: CredentialMapper,
    private val userRepository: UserRepository,
) : WriteCredentialPort, GetCredentialPort {
    override fun registerCredential(credential: Credential): Credential {
        val credentialEntity =
            credentialRepository.save(
                CredentialEntity(
                    credentialNo = credential.credentialNo,
                    userId = credential.userId,
                    credentialEmail = credential.credentialEmail,
                    provider = credential.provider,
                    userEntity = userRepository.getReferenceById(credential.user.userNo),
                    state = credential.state,
                ),
            )
        return credentialMapper.mapToDomainEntity(credentialEntity)
    }

    override fun findExistsCredentialByUserIdAndProvider(
        userId: String,
        provider: Provider,
    ): Boolean {
        return credentialRepository.findExistsUserIdByUserIdAndProvider(userId, provider)
    }

    override fun findCredentialByUserIdAndProvider(
        userId: String,
        provider: Provider,
    ): Credential? {
        return credentialRepository.findCredentialEntityByUserIdAndProvider(userId, provider)
            ?.let { credentialMapper.mapToDomainEntity(it) }
    }
}
