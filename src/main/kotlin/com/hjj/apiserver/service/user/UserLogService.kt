package com.hjj.apiserver.service.user

import com.hjj.apiserver.domain.user.UserLog
import com.hjj.apiserver.repository.user.UserLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserLogService(
    private val userLogRepository: UserLogRepository,
) {

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun insertUserLog(userLog: UserLog){
        userLogRepository.save(userLog)
    }
}