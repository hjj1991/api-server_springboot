package com.hjj.apiserver.adapter.out.persistence.auth.repository

import com.hjj.apiserver.adapter.out.persistence.auth.entity.UserDeletionRequestEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserDeletionRequestRepository : JpaRepository<UserDeletionRequestEntity, Long>
