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

        return jpaQueryFactory
//            .select(
//            Projections.constructor(
//                AccountBookDetailResponse::class.java,
//                accountBook.accountBookName,
//                Projections.constructor(
//                    AccountBookDetailResponse.CategoryDetail::class.java,
//                    accountBook.categories.any().categoryNo,
//                    accountBook.categories.any().categoryName,
//                    accountBook.categories.any().categoryIcon,
//                    accountBook.accountBookName,
//                    Projections.constructor(
//                        AccountBookDetailResponse.CategoryDetail::class.java,
//                    ),
//
//                )
//            )
//        )
            .selectFrom(accountBook)
            .innerJoin(accountBook.categories, category)
            .leftJoin(category.childCategories, childCategory).on(category.categoryNo.eq(childCategory.parentCategory.categoryNo))
            .where(accountBook.accountBookNo.eq(accountBookNo)
                    .and(category.parentCategory.isNull))
            .distinct()
            .transform(groupBy(
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
                            category.accountBook.accountBookNo,
                            category.accountBook.accountBookName,
                        )
                    )
                )
            )
            )[0]
    }
}