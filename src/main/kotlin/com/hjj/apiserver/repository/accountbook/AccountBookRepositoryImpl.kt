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
        val childAccountBook = QAccountBook("childAccountBook")
        val childAccountBook2 = QAccountBook("childAccountBook2")

        val transform = jpaQueryFactory
            .selectFrom(accountBook)
            .innerJoin(accountBook.categories, category)
            .leftJoin(category.childCategories, childCategory)
            .leftJoin(category.accountBook, childAccountBook2)
            .leftJoin(childCategory.accountBook, childAccountBook)
            .where(
                accountBook.accountBookNo.eq(accountBookNo)
                    .and(category.parentCategory.isNull)
            )
            .distinct()
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
                                category.accountBook.accountBookNo,
                                category.accountBook.accountBookName,
                                list(
                                    Projections.constructor(
                                        AccountBookDetailResponse.ChildrenCategory::class.java,
                                        childCategory.categoryNo,
                                        childCategory.categoryName,
                                        childCategory.categoryIcon,
                                        childCategory.accountBook.accountBookNo,
                                        childCategory.accountBook.accountBookName,
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