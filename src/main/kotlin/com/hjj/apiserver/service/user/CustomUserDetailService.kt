package com.hjj.apiserver.service.user

import com.hjj.apiserver.common.exception.UserNotFoundException
import com.hjj.apiserver.repository.user.UserRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailService(
        private val userRepository: UserRepository,
): UserDetailsService{

    @Cacheable(value = ["users"], key = "#username")
    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByIdOrNull(username.toLong())?: throw UserNotFoundException()
    }

}