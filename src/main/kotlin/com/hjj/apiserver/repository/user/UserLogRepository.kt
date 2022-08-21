package com.hjj.apiserver.repository.user

import com.hjj.apiserver.domain.user.UserLog
import org.springframework.data.jpa.repository.JpaRepository

interface UserLogRepository: JpaRepository<UserLog, Long> {
}