package com.hjj.apiserver.service

import com.hjj.apiserver.common.exception.AlreadyExistedUserException
import com.hjj.apiserver.common.exception.UserNotFoundException
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.user.UserAttribute
import com.hjj.apiserver.repository.user.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class SocialUserAuthService(
    private val userRepository: UserRepository,
) : UserAuthService {

    @Transactional(readOnly = false)
    override fun register(userAttribute: UserAttribute): User {
        userRepository.findByProviderAndProviderId(userAttribute.provider!!, userAttribute.providerId!!)
            ?.also { throw AlreadyExistedUserException() }

        val nickName = userRepository.findByNickName(userAttribute.nickName)?.let {
            Random().ints(97, 123)
                .limit(10).collect({ StringBuilder() }, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString()
        } ?: userAttribute.nickName

        return kotlin.runCatching {
            val newUser = userAttribute.toUserEntity(nickName)
            userRepository.saveAndFlush(newUser)
        }.onFailure { exception ->
            when (exception) {
                is DataIntegrityViolationException -> throw AlreadyExistedUserException()
                else -> throw exception
            }
        }.getOrThrow()
    }

    override fun signIn(userAttribute: UserAttribute): User {
        return userRepository.findByProviderAndProviderId(userAttribute.provider!!, userAttribute.providerId!!)
            ?: throw UserNotFoundException()
    }

    override fun isMatchingProvider(provider: Provider?): Boolean {
        return provider != null
    }
}