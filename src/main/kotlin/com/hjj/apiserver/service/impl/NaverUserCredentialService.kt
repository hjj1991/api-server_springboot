// package com.hjj.apiserver.service.impl
//
// import com.hjj.apiserver.common.exception.AlreadyExistedUserException
// import com.hjj.apiserver.domain.user.Provider
// import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
// import com.hjj.apiserver.dto.user.UserAttribute
// import com.hjj.apiserver.adapter.out.persistence.user.UserRepository
// import com.hjj.apiserver.service.UserCredentialService
// import org.springframework.dao.DataIntegrityViolationException
// import org.springframework.stereotype.Service
// import org.springframework.transaction.annotation.Transactional
// import java.util.*
//
// @Service
// @Transactional(readOnly = true)
// class NaverUserCredentialService(
//    private val userRepository: UserRepository,
// ) : UserCredentialService {
//
//    @Transactional(readOnly = false)
//    override fun register(userAttribute: UserAttribute): UserEntity {
// //        userRepository.findByProviderAndProviderId(userAttribute.provider!!, userAttribute.providerId!!)
// //            ?.also { throw AlreadyExistedUserException() }
//
//        val nickName = userRepository.findByNickName(userAttribute.nickName!!)?.let {
//            Random().ints(97, 123)
//                .limit(10).collect({ StringBuilder() }, StringBuilder::appendCodePoint, StringBuilder::append)
//                .toString()
//        } ?: userAttribute.nickName
//
//        return kotlin.runCatching {
//            val newUser = userAttribute.toUserEntity(nickName)
//            userRepository.saveAndFlush(newUser)
//        }.onFailure { exception ->
//            when (exception) {
//                is DataIntegrityViolationException -> throw AlreadyExistedUserException()
//                else -> throw exception
//            }
//        }.getOrThrow()
//    }
//
//    override fun signIn(userAttribute: UserAttribute): UserEntity {
//        return UserEntity(userId = "124", nickName = "124")
// //        return userRepository.findByProviderAndProviderId(userAttribute.provider!!, userAttribute.providerId!!)
// //            ?: throw UserNotFoundException()
//    }
//
//    override fun isMatchingProvider(provider: Provider?): Boolean {
//        return provider != null
//    }
// }
