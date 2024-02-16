package com.hjj.apiserver.dto.category.response

import com.hjj.apiserver.domain.category.Category

data class CategoryAddResponse(
    val accountBookNo: Long,
    val categoryNo: Long,
    val parentCategoryNo: Long? = null,
    val categoryName: String,
    val categoryDesc: String,
    val categoryIcon: String,
) {
    companion object {
        fun of(category: Category): CategoryAddResponse {
            return CategoryAddResponse(
                accountBookNo = category.accountBook.accountBookNo!!,
                categoryNo = category.categoryNo!!,
                parentCategoryNo = category.parentCategory?.categoryNo,
                categoryName = category.categoryName,
                categoryDesc = category.categoryDesc,
                categoryIcon = category.categoryIcon,
            )
        }
    }
}
