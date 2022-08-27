package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AccountBookRepository: JpaRepository<AccountBook, Long>, AccountBookRepositoryCustom {

    @Query(
        "select ab from AccountBook ab where ab.accountBookNo = " +
                "(select abu.accountBook.accountBookNo from AccountBookUser abu " +
                "where abu.user.userNo = :userNo and abu.accountBook.accountBookNo = :accountBookNo and abu.accountRole = :accountRole)"
    )
    fun findAccountBookBySubQuery(@Param("userNo") userNo:Long, @Param("accountBookNo") accountBookNo: Long, @Param("accountRole") accountRole: AccountRole): AccountBook?


    @Query(
        "select ab from AccountBook ab where ab.accountBookNo = " +
                "(select abu.accountBook.accountBookNo from AccountBookUser abu " +
                "where abu.user.userNo = :userNo and abu.accountBook.accountBookNo = :accountBookNo and abu.accountRole in ('OWNER', 'MEMBER'))"
    )
    fun findAccountBookBySubQuery(
        @Param("userNo") userNo: Long,
        @Param("accountBookNo") accountBookNo: Long
    ): AccountBook?



}