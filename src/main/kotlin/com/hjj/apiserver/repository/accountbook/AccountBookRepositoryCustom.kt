package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.dto.accountbook.response.AccountBookDetailResponse

interface AccountBookRepositoryCustom {

    fun findAccountBookDetail(accountBookNo: Long): AccountBookDetailResponse?
}