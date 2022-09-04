package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.AccountBook
import org.springframework.data.jpa.repository.JpaRepository

interface AccountBookRepository: JpaRepository<AccountBook, Long>, AccountBookRepositoryCustom {

}