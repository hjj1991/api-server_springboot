package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.accountbook.QAccountBook.accountBook
import com.hjj.apiserver.domain.accountbook.QAccountBookUser.accountBookUser
import com.hjj.apiserver.domain.category.QCategory.category
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory

class AccountBookRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
): AccountBookRepositoryCustom {

    override fun findAccountBook(userNo: Long, accountBookNo: Long, accountRoles: List<AccountRole>): AccountBook?{
        return jpaQueryFactory
            .select(accountBook)
            .from(accountBook)
            .distinct()
            .innerJoin(accountBook.categories, category).fetchJoin()
            .where(
                accountBook.accountBookNo.eq(
                    JPAExpressions
                    .select(accountBookUser.accountBook.accountBookNo)
                    .from(accountBookUser)
                    .where(
                        accountBookUser.user.userNo.eq(userNo),
                        accountBookUser.accountRole.`in`(accountRoles)
                    )
                )
            )
            .fetchOne()
    }
}