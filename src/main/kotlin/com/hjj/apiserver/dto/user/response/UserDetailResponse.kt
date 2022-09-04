package com.hjj.apiserver.dto.user.response

import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.Role
import java.time.LocalDateTime

class UserDetailResponse(
    val userNo: Long,
    val userId: String? = null,
    val nickName: String,
    val userEmail: String? = null,
    val provider: Provider? = null,
    val role: Role,
    val providerConnectDate: LocalDateTime? = null,
    val createdDate: LocalDateTime,
    val lastLoginDateTime: LocalDateTime? = null,
) {
}