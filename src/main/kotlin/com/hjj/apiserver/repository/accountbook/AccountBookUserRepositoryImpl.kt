package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.QAccountBook.*
import com.hjj.apiserver.domain.accountbook.QAccountBookUser.*
import com.hjj.apiserver.domain.user.QUser
import com.hjj.apiserver.domain.user.QUser.*
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class AccountBookUserRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
): AccountBookUserRepositoryCustom {
    override fun findAllAccountBookByUserNo(userNo: Long): List<AccountBookFindAllResponse> {
        jpaQueryFactory.select(
            Projections.constructor(
                AccountBookFindAllResponse::class.java,
                accountBook.accountBookNo,
                accountBook.accountBookName,
                accountBook.accountBookDesc,
                accountBookUser.backGroundColor,
                accountBookUser.color,
                accountBookUser.accountRole,
                user.a
            )
        )
            .from(accountBookUser)
            .innerJoin(accountBookUser.accountBook, accountBook)
            .leftJoin(accountBookUser.user, user)

    }
}