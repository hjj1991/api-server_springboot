package com.hjj.apiserver.service.impl

import com.hjj.apiserver.adapter.out.persistence.user.entity.UserLogEntity
import com.hjj.apiserver.adapter.out.persistence.user.repository.UserLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserLogService(
    private val userLogRepository: UserLogRepository,
) {
    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun addUserLog(userLogEntity: UserLogEntity) {
        userLogRepository.save(userLogEntity)
    }
}
