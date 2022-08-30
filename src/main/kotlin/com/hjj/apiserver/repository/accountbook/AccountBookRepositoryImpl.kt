package com.hjj.apiserver.repository.accountbook

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.QAccountBook.*
import com.hjj.apiserver.domain.category.QCategory.*
import com.querydsl.jpa.impl.JPAQueryFactory

class AccountBookRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val objectMapper: ObjectMapper,
): AccountBookRepositoryCustom {
    override fun findAccountBookByAccountBookNo(accountBookNo: Long): AccountBook? {
        return jpaQueryFactory
            .selectFrom(accountBook)
            .distinct()
            .innerJoin(accountBook.categories, category).fetchJoin()
            .where(
                accountBook.accountBookNo.eq(accountBookNo)
            ).fetchOne()
    }
}