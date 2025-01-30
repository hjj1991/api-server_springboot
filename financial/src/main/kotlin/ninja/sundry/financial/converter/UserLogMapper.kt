package com.hjj.apiserver.converter

import com.hjj.apiserver.adapter.out.persistence.user.entity.UserLogEntity
import com.hjj.apiserver.domain.user.UserLog
import org.springframework.stereotype.Component

@Component
class UserLogMapper(
    private val userMapper: UserMapper,
) {
    fun mapToDomainEntity(userLogEntity: UserLogEntity): UserLog {
        return UserLog(
            userLogNo = userLogEntity.userLogNo,
            logType = userLogEntity.logType,
            user = userMapper.mapToDomainEntity(userLogEntity.userEntity),
        )
    }
}
