package com.hjj.apiserver.adapter.out.persistence.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserLogRepository : JpaRepository<UserLogEntity, Long>
