package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse

interface AccountBookUserRepositoryCustom {
    fun findAllAccountBookByUserNo(userNo: Long): List<AccountBookFindAllResponse>

    fun findAccountRole(
        userNo: Long,
        accountBookNo: Long,
    ): AccountRole?
}
