package com.hjj.apiserver.dto.accountbook.response

import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole

data class AccountBookAddResponse(
    val accountBookNo: Long,
    val accountBookName: String,
    val accountBookDesc: String,
    val backGroundColor: String,
    val color: String,
    val accountRole: AccountRole,
) {
    companion object {
        fun of(savedAccountBookUser: AccountBookUser): AccountBookAddResponse {
            return AccountBookAddResponse(
                accountBookNo = savedAccountBookUser.accountBook.accountBookNo!!,
                accountBookName = savedAccountBookUser.accountBook.accountBookName,
                accountBookDesc = savedAccountBookUser.accountBook.accountBookDesc,
                backGroundColor = savedAccountBookUser.backGroundColor,
                color = savedAccountBookUser.color,
                accountRole = savedAccountBookUser.accountRole,
            )
        }
    }
}
