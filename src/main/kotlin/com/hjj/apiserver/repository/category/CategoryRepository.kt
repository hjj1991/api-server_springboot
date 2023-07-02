package com.hjj.apiserver.repository.category

import com.hjj.apiserver.domain.category.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long>, CategoryRepositoryCustom {
    fun findCategoryByCategoryNoAndAccountBook_AccountBookNoAndIsDeleteIsFalse(
        categoryNo: Long,
        accountBookNo: Long
    ): Category?

}