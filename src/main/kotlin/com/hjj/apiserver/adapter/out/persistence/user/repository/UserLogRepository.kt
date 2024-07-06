package com.hjj.apiserver.adapter.out.persistence.user.repository

import com.hjj.apiserver.adapter.out.persistence.user.entity.UserLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserLogRepository : JpaRepository<UserLogEntity, Long>
