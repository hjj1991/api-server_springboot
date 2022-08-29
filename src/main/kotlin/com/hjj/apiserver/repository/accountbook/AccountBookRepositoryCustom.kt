package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.AccountBook

interface AccountBookRepositoryCustom {

    fun findAccountBookByAccountBookNo(accountBookNo: Long): AccountBook?
}