package com.hjj.apiserver.adapter.out.persistence.user.repository

interface UserRepositoryCustom {
    //    fun findUserDetail(userNo: Long): UserDetailResponse?
    fun existsByUserNickName(nickName: String): Boolean
}
