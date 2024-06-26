package com.hjj.apiserver.dto.user.response

import com.hjj.apiserver.domain.user.Role
import java.time.LocalDateTime

class UserDetailResponse(
    val userNo: Long,
    val userId: String? = null,
    val nickName: String,
    val userEmail: String? = null,
    val role: Role,
    val picture: String? = null,
    val createdDate: LocalDateTime,
    val lastLoginDateTime: LocalDateTime? = null,
)
