package com.hjj.apiserver.service.impl

import com.hjj.apiserver.adapter.out.persistence.user.entity.UserEntity
import com.hjj.apiserver.adapter.out.persistence.user.repository.UserRepository
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.dto.user.UserAttribute
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Random

@Service
@Transactional(readOnly = true)
class NaverUserCredentialService(
    private val userRepository: UserRepository,
) {
    @Transactional(readOnly = false)
    fun register(userAttribute: UserAttribute): UserEntity {
        val nickName =
            userRepository
                .findByNickName(userAttribute.nickName!!)
                ?.let {
                    Random()
                        .ints(97, 123)
                        .limit(10)
                        .collect({ StringBuilder() }, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString()
                } ?: userAttribute.nickName

        return kotlin.runCatching {
            val newUser = userAttribute.toUserEntity(nickName)
            userRepository.saveAndFlush(newUser)
        }.getOrThrow()
    }

    fun signIn(userAttribute: UserAttribute): UserEntity {
        return UserEntity(nickName = "124")
        //        return userRepository.findByProviderAndProviderId(userAttribute.provider!!, userAttribute.providerId!!)
        //            ?: throw UserNotFoundException()
    }

    fun isMatchingProvider(provider: Provider?): Boolean {
        return provider != null
    }
}
