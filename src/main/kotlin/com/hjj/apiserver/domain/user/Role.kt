package com.hjj.apiserver.domain.user

import java.time.Clock
import java.time.ZonedDateTime

data class Role(
    val id: Long = 0L,
    val roleType: RoleType,
    val createdAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC()),
    val modifiedAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC())
)
