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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
        val map = Flux.just(2, 5, 6).map { abc -> abc }
        val flatMap = Flux.just(2, 5, 6).flatMap { abc -> Mono.just(abc) }
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
