package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.QAccountBook
import com.hjj.apiserver.domain.accountbook.QAccountBook.*
import com.hjj.apiserver.domain.category.QCategory
import com.hjj.apiserver.domain.category.QCategory.*
import com.hjj.apiserver.dto.accountbook.response.AccountBookDetailResponse
import com.querydsl.core.group.GroupBy
import com.querydsl.core.group.GroupBy.*
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class AccountBookRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
): AccountBookRepositoryCustom {
    override fun findAccountBookDetail(accountBookNo: Long): AccountBookDetailResponse? {

        val childCategory = QCategory("childCategory")

        val transform = jpaQueryFactory
            .selectFrom(accountBook)
            .innerJoin(accountBook.categories, category)
            .leftJoin(category.childCategories, childCategory)
            .where(
                accountBook.accountBookNo.eq(accountBookNo)
                    .and(category.parentCategory.isNull)
            )
            .transform(
                groupBy(
                    accountBook.accountBookName,
                ).list(
                    Projections.constructor(
                        AccountBookDetailResponse::class.java,
                        accountBook.accountBookName,
                        list(
                            Projections.constructor(
                                AccountBookDetailResponse.CategoryDetail::class.java,
                                category.categoryNo,
                                category.categoryName,
                                category.categoryIcon,
                                list(
                                    Projections.constructor(
                                        AccountBookDetailResponse.ChildrenCategory::class.java,
                                        childCategory.categoryNo,
                                        childCategory.categoryName,
                                        childCategory.categoryIcon,
                                        childCategory.parentCategory.categoryNo
                                    )
                                )
                            )
                        )
                    )
                )
            )

        return transform[0]
    }
}