package com.hjj.apiserver.adapter.out.persistence.user.repository

import com.hjj.apiserver.adapter.out.persistence.user.entity.SnsAccountEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SnsAccountRepository : JpaRepository<SnsAccountEntity, Long>
