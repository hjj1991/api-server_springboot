package com.hjj.apiserver.domain.user

import java.time.Clock
import java.time.ZonedDateTime

data class UserLog(
    val id: Long = 0L,
    val logType: LogType,
    val userId: Long,
    val createdAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC()),
    val modifiedAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC())
)
