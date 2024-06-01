package com.hjj.apiserver.converter

import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
import com.hjj.apiserver.domain.user.User
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun mapToDomainEntity(userEntity: UserEntity): User {
        return User(
            userNo = userEntity.userNo,
            nickName = userEntity.nickName,
            userEmail = userEntity.userEmail,
            userPw = userEntity.userPw,
            picture = userEntity.picture,
            role = userEntity.role,
        )
    }
}
