package com.hjj.apiserver.repository.user

import com.hjj.apiserver.dto.user.response.UserDetailResponse

interface UserRepositoryCustom {
    fun findUserDetail(userNo: Long): UserDetailResponse?
    fun findExistsUserNickName(nickName: String): Boolean
    fun findExistsUserId(userId: String): Boolean
}