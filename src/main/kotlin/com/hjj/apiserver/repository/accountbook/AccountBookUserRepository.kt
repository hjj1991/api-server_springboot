package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.AccountBookUser
import org.springframework.data.jpa.repository.JpaRepository

interface AccountBookUserRepository : JpaRepository<AccountBookUser, Long>, AccountBookUserRepositoryCustom {
//    fun findFirstByUser_UserNo(userNo: Long):AccountBookUser
}
