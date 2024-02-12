package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.application.port.out.user.WriteUserLogPort
import com.hjj.apiserver.common.PersistenceAdapter
import com.hjj.apiserver.converter.UserLogMapper
import com.hjj.apiserver.domain.user.UserLog

@PersistenceAdapter
class UserLogPersistenceAdapter(
    private val userLogRepository: UserLogRepository,
    private val userRepository: UserRepository,
    private val userLogMapper: UserLogMapper,
): WriteUserLogPort {
    override fun registerUserLog(userLog: UserLog): UserLog {
        val userLogEntity = userLogRepository.save(
            UserLogEntity(
                logType = userLog.logType,
                userEntity = userRepository.getReferenceById(userLog.user.userNo)
            )
        )
        return userLogMapper.mapToDomainEntity(userLogEntity)
    }
}