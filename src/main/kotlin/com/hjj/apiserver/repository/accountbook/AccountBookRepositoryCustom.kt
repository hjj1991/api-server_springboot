package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountRole

interface AccountBookRepositoryCustom {
    fun findAccountBook(
        userNo: Long,
        accountBookNo: Long,
        accountRoles: List<AccountRole> = listOf(
            AccountRole.OWNER,
            AccountRole.GUEST,
            AccountRole.MEMBER
        )
    ): AccountBook?
}