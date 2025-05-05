package com.hjj.apiserver.domain.user

import java.time.Clock
import java.time.ZonedDateTime

data class UserRole(
    val id: Long = 0L,
    val userId: Long,
    val roleId: Long,
    val createdAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC()),
    val modifiedAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC()),
)
