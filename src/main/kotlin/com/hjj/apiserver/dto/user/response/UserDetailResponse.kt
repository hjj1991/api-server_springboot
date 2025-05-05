package com.hjj.apiserver.dto.user.response

import com.hjj.apiserver.domain.user.RoleType
import java.time.LocalDateTime

class UserDetailResponse(
    val userNo: Long,
    val userId: String? = null,
    val nickName: String,
    val userEmail: String? = null,
    val roleType: RoleType,
    val picture: String? = null,
    val createdDate: LocalDateTime,
    val lastLoginDateTime: LocalDateTime? = null,
)
