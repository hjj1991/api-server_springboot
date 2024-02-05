package com.hjj.apiserver.application.service

import com.hjj.apiserver.application.port.`in`.user.RegisterCredentialCommand
import com.hjj.apiserver.application.port.`in`.user.UserCredentialUseCase
import com.hjj.apiserver.application.port.out.user.GetCredentialPort
import com.hjj.apiserver.application.port.out.user.WriteCredentialPort
import com.hjj.apiserver.common.exception.AlreadyExistedUserException
import com.hjj.apiserver.domain.user.Credential
import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.user.UserAttribute
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("generalUserCredentialService")
@Transactional(readOnly = true)
class GeneralUserCredentialService(
    private val writeCredentialPort: WriteCredentialPort,
    private val getCredentialPort: GetCredentialPort,
) : UserCredentialUseCase {

    @Transactional(readOnly = false)
    override fun register(registerCredentialCommand: RegisterCredentialCommand): Credential {
        return kotlin.runCatching {
            val credential = Credential(
                userId = registerCredentialCommand.userId,
                credentialEmail = registerCredentialCommand.userEmail,
                user = registerCredentialCommand.user,
                provider = registerCredentialCommand.provider,
            )
            writeCredentialPort.registerCredential(credential)
        }.onFailure { exception ->
            when (exception) {
                is DataIntegrityViolationException -> throw AlreadyExistedUserException()
                else -> throw exception
            }
        }.getOrThrow()
    }

    override fun signIn(userAttribute: UserAttribute): Credential {
//        val user = userRepository.findByUserId(userAttribute.userId!!) ?: throw UserNotFoundException()
//
//        /* SNS 로그인 계정인 경우 Exception처리 */
////        if (user.isSocialUser()) {
////            throw ExistedSocialUserException()
////        }
//
//        if (!passwordEncoder.matches(userAttribute.userPw, user.userPw)) {
//            throw BadCredentialsException("패스워드가 일치하지 않습니다.")
//        }
//
//        return user
        return Credential(userId="!24124", provider=Provider.GENERAL, user = User(nickName = "sample"))
    }

    override fun isMatchingProvider(provider: Provider): Boolean {
        return provider == Provider.GENERAL
    }

    override fun existsUserId(userId: String): Boolean {
        return getCredentialPort.findExistsCredentialByUserIdAndProvider(userId, Provider.GENERAL)
    }
}