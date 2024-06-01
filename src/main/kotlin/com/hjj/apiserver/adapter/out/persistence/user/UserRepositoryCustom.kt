package com.hjj.apiserver.adapter.out.persistence.user

interface UserRepositoryCustom {
    //    fun findUserDetail(userNo: Long): UserDetailResponse?
    fun findExistsUserNickName(nickName: String): Boolean
}
