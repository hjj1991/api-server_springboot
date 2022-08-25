package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface AccountBookUserRepository: JpaRepository<AccountBookUser, Long>, AccountBookUserRepositoryCustom {

    @EntityGraph(attributePaths = ["accountBookEntity", "userEntity"])
    fun findGraphByUser_userNo(userNo: Long): MutableList<AccountBookUser>

    @EntityGraph(attributePaths = ["accountBookEntity", "userEntity"])
    fun findEntityGraphByAccountBook_accountBookNoIn(accountBookNoList: List<Long>): MutableList<AccountBookUser>


    fun findByUser_UserNoAndAccountBook_AccountBookNo(
        userNo: Long,
        accountBookNo: Long
    ): AccountBookUser?

    fun existsByUser_UserNoAndAccountBook_AccountBookNoAndAccountRole(
        userNo: Long,
        accountBookNo: Long,
        accountRole: AccountRole
    ): Boolean?
}