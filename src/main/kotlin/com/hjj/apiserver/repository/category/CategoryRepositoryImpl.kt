package com.hjj.apiserver.repository.category

import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.accountbook.QAccountBookUser.accountBookUser
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.domain.category.QCategory
import com.hjj.apiserver.domain.category.QCategory.category
import com.hjj.apiserver.dto.category.response.CategoryFindAllResponse
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory

class CategoryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CategoryRepositoryCustom {
    override fun findCategories(userNo: Long, accountBookNo: Long): List<CategoryFindAllResponse.Categories> {
        val childCategory = QCategory("childrenCategory")
        return jpaQueryFactory
            .selectFrom(category)
            .leftJoin(category.childCategories, childCategory)
            .where(
                category.parentCategory.categoryNo.isNull
                    .and(
                        category.accountBook.accountBookNo.eq(
                            JPAExpressions.select(accountBookUser.accountBook.accountBookNo)
                                .from(accountBookUser)
                                .where(
                                    accountBookUser.accountBook.accountBookNo.eq(accountBookNo)
                                        .and(accountBookUser.user.userNo.eq(userNo))
                                        .and(accountBookUser.accountRole.ne(AccountRole.GUEST))
                                )
                        )
                    )
            )
            .distinct()
            .transform(
                groupBy(
                    category.categoryNo,
                    category.categoryName,
                    category.categoryDesc,
                    category.categoryIcon,
                    category.accountBook.accountBookNo,
                ).list(
                    Projections.constructor(
                        CategoryFindAllResponse.Categories::class.java,
                        category.categoryNo,
                        category.categoryName,
                        category.categoryDesc,
                        category.categoryIcon,
                        category.accountBook.accountBookNo,
                        list(
                            Projections.constructor(
                                CategoryFindAllResponse.ChildCategory::class.java,
                                childCategory.categoryNo,
                                childCategory.categoryName,
                                childCategory.categoryDesc,
                                childCategory.categoryIcon,
                                childCategory.accountBook.accountBookNo,
                                childCategory.parentCategory.categoryNo,
                            ).skipNulls()
                        )
                    )
                )
            )
    }

    override fun findCategoryByAccountRole(categoryNo: Long, accountBookNo: Long, userNo: Long, accountRoles: Set<AccountRole>): Category? {
        val parentCategory = QCategory("parentCategory")
        return jpaQueryFactory.select(category)
            .from(category)
            .leftJoin(category.parentCategory, parentCategory).fetchJoin()
            .where(
                category.categoryNo.eq(categoryNo)
                    .and(
                        category.accountBook.accountBookNo.eq(
                            JPAExpressions.select(accountBookUser.accountBook.accountBookNo)
                                .from(accountBookUser)
                                .where(
                                    accountBookUser.user.userNo.eq(userNo),
                                    accountBookUser.accountBook.accountBookNo.eq(accountBookNo)
                                        .and(accountBookUser.accountRole.`in`(accountRoles))
                                )
                        )
                    )

            ).fetchOne()
    }


}