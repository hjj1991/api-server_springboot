package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.adapter.out.persistence.user.entity.UserEntity
import com.hjj.apiserver.adapter.out.persistence.user.repository.UserRepository
import com.hjj.apiserver.application.port.out.user.GetUserPort
import com.hjj.apiserver.application.port.out.user.WriteUserPort
import com.hjj.apiserver.common.PersistenceAdapter
import com.hjj.apiserver.converter.UserMapper
import com.hjj.apiserver.domain.user.User

@PersistenceAdapter
class UserPersistenceAdapter(
    val userRepository: UserRepository,
    val userMapper: UserMapper,
) : GetUserPort, WriteUserPort {
    override fun findExistsUserNickName(nickName: String): Boolean {
        return userRepository.findExistsUserNickName(nickName)
    }

    override fun registerUser(user: User): User {
        val userEntity =
            userRepository.save(
                UserEntity(
                    nickName = user.nickName,
                    userEmail = user.userEmail,
                    userPw = user.userPw,
                    role = user.role,
                    picture = user.picture,
                ),
            )
        return userMapper.mapToDomainEntity(userEntity)
    }
}
