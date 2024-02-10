package com.hjj.apiserver.domain.user

import java.time.Clock
import java.time.ZonedDateTime

class UserLog(
    val userLogNo: Long = 0L,
    val logType: LogType,
    val user: User,
    val createdAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC()),
    val modifiedAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC()),
) {
}