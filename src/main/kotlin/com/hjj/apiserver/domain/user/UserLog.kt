package com.hjj.apiserver.domain.user

import java.time.LocalDateTime

class UserLog(
    val userLogNo: Long = 0L,
    val logDateTime: LocalDateTime = LocalDateTime.now(),

) {
}