package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.adapter.out.persistence.user.UserLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserLogRepository: JpaRepository<UserLogEntity, Long> {
}