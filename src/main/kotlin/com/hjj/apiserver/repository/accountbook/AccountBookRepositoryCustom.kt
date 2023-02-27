package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.dto.accountbook.AccountBookDto

interface AccountBookRepositoryCustom {
    fun findAccountBook(
        userNo: Long,
        accountBookNo: Long,
    ): AccountBookDto?
}