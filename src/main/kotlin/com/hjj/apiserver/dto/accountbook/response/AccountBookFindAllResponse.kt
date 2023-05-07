package com.hjj.apiserver.dto.accountbook.response

import com.hjj.apiserver.domain.accountbook.AccountRole

data class AccountBookFindAllResponse(
    val accountBookNo: Long,
    val accountBookName: String,
    val accountBookDesc: String,
    val backGroundColor: String,
    val color: String,
    val accountRole: AccountRole,
    val joinedUsers: List<JoinedUser> = mutableListOf()
) {

    data class JoinedUser(
        val userNo: Long,
        val nickName: String,
        val picture: String?,
    )
}