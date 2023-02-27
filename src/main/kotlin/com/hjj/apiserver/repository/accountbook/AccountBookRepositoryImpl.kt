package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.QAccountBook.Companion.accountBook
import com.hjj.apiserver.domain.accountbook.QAccountBookUser.Companion.accountBookUser
import com.hjj.apiserver.dto.accountbook.AccountBookDto
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class AccountBookRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : AccountBookRepositoryCustom {

    override fun findAccountBook(userNo: Long, accountBookNo: Long): AccountBookDto? {
        return jpaQueryFactory.select(
            Projections.constructor(
                AccountBookDto::class.java,
                accountBook.accountBookNo,
                accountBook.accountBookName,
                accountBook.accountBookDesc,
                accountBookUser.backGroundColor,
                accountBookUser.color,
                accountBookUser.accountRole,
                accountBook.createdDate,
            )
        ).from(accountBook)
            .join(accountBookUser).on(accountBook.accountBookNo.eq(accountBookUser.accountBook.accountBookNo))
            .where(accountBook.accountBookNo.eq(accountBookNo))
            .fetchOne()
    }

}