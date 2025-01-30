package com.hjj.apiserver.dto.accountbook

import com.fasterxml.jackson.annotation.JsonFormat
import com.hjj.apiserver.domain.accountbook.AccountRole
import java.time.ZonedDateTime

data class AccountBookDto(
    val accountBookNo: Long,
    val accountBookName: String,
    val accountBookDesc: String,
    val backgroundColor: String,
    val color: String,
    val accountRole: AccountRole,
    @field:JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    val createdAt: ZonedDateTime,
)
