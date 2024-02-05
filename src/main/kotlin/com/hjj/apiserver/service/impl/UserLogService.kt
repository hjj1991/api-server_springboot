package com.hjj.apiserver.service.impl

import com.hjj.apiserver.adapter.out.persistence.user.UserLogEntity
import com.hjj.apiserver.repository.user.UserLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserLogService(
    private val userLogRepository: UserLogRepository,
) {

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun addUserLog(userLogEntity: UserLogEntity){
        userLogRepository.save(userLogEntity)
    }
}