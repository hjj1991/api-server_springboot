package com.hjj.apiserver.service

import com.hjj.apiserver.common.exception.AlreadyExistedUserException
import com.hjj.apiserver.common.exception.ExistedSocialUserException
import com.hjj.apiserver.common.exception.UserNotFoundException
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.user.UserAttribute
import com.hjj.apiserver.repository.user.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GeneralUserAuthService(
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
) : UserAuthService {

    @Transactional(readOnly = false)
    override fun register(userAttribute: UserAttribute): User {
        return kotlin.runCatching {
            val newUser = userAttribute.toEncryptPwUserEntity(passwordEncoder)
            userRepository.saveAndFlush(newUser)
        }.onFailure { exception ->
            when (exception) {
                is DataIntegrityViolationException -> throw AlreadyExistedUserException()
                else -> throw exception
            }
        }.getOrThrow()
    }

    override fun signIn(userAttribute: UserAttribute): User {
        val user = userRepository.findByUserId(userAttribute.userId!!) ?: throw UserNotFoundException()

        /* SNS 로그인 계정인 경우 Exception처리 */
        if (user.isSocialUser()) {
            throw ExistedSocialUserException()
        }

        if (!passwordEncoder.matches(userAttribute.userPw, user.userPw)) {
            throw BadCredentialsException("패스워드가 일치하지 않습니다.")
        }

        return user
    }

    override fun isMatchingProvider(provider: Provider?): Boolean {
        return provider == null
    }
}