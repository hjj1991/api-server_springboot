package com.hjj.apiserver.dto.accountbook

import com.hjj.apiserver.domain.accountbook.AccountRole
import java.time.LocalDateTime

data class AccountBookDto(
    val accountBookNo: Long,
    val accountBookName: String,
    val accountBookDesc: String,
    val backgroundColor: String,
    val color: String,
    val accountRole: AccountRole,
    val createdDate: LocalDateTime,
) {
}