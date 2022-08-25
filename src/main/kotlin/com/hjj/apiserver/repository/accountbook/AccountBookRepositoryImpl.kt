package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.QAccountBook
import com.hjj.apiserver.domain.accountbook.QAccountBook.*
import com.hjj.apiserver.domain.category.QCategory
import com.hjj.apiserver.domain.category.QCategory.*
import com.hjj.apiserver.dto.accountbook.response.AccountBookDetailResponse
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class AccountBookRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
): AccountBookRepositoryCustom {
    override fun findAccountBookDetail(accountBookNo: Long): AccountBookDetailResponse? {

        val childCategory = QCategory("childCategory")

        return jpaQueryFactory.select(
            Projections.constructor(
                AccountBookDetailResponse::class.java,
                accountBook.accountBookName,
                accountBook.categories
            )
        )
            .distinct()
            .from(accountBook)
            .leftJoin(accountBook.categories, category)
            .leftJoin(category.childCategories, childCategory)
            .where(accountBook.accountBookNo.eq(accountBookNo)
                    .and(category.parentCategory.isNull))
            .fetchOne()
    }
}