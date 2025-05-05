package com.hjj.apiserver.domain.user

import java.time.Clock
import java.time.ZonedDateTime

data class User(
    val id: Long = 0L,
    val username: String?,
    val password: String?,
    val nickName: String,
    val userEmail: String? = null,
    val pictureUrl: String? = null,
    val deletedAt: ZonedDateTime? = null,
    val createdAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC()),
    val modifiedAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC())
)
