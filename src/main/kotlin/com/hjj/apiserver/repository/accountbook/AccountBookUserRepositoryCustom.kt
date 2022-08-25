package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse

interface AccountBookUserRepositoryCustom {
    fun findAllAccountBookByUserNo(userNo: Long):List<AccountBookFindAllResponse>
}