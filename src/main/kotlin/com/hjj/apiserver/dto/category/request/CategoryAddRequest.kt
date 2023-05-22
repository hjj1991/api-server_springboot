package com.hjj.apiserver.dto.category.request

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.category.Category
import jakarta.validation.constraints.NotEmpty

data class CategoryAddRequest(
    val accountBookNo: Long,
    val parentCategoryNo: Long? = null,
    @field:NotEmpty
    val categoryName: String,
    val categoryDesc: String,
    val categoryIcon: String,
) {

    fun toEntity(accountBook: AccountBook, parentCategory: Category?):Category {
        return Category(
            categoryName = categoryName,
            categoryDesc = categoryDesc,
            categoryIcon = categoryIcon,
            accountBook = accountBook,
            parentCategory = parentCategory
        )
    }

}