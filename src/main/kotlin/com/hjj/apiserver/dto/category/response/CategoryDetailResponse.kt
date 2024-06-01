package com.hjj.apiserver.dto.category.response

import com.hjj.apiserver.domain.category.Category
import java.time.ZonedDateTime

data class CategoryDetailResponse(
    val accountBookNo: Long,
    val categoryNo: Long,
    val parentCategoryNo: Long? = null,
    val categoryName: String,
    val categoryDesc: String,
    val categoryIcon: String,
    val childCategories: List<ChildCategory> = listOf(),
) {
    companion object {
        fun of(category: Category): CategoryDetailResponse {
            return CategoryDetailResponse(
                accountBookNo = category.accountBook.accountBookNo!!,
                categoryNo = category.categoryNo!!,
                categoryName = category.categoryName,
                categoryDesc = category.categoryDesc,
                categoryIcon = category.categoryIcon,
                childCategories =
                    category.childCategories.map {
                        ChildCategory(
                            accountBookNo = it.accountBook.accountBookNo!!,
                            categoryNo = it.categoryNo!!,
                            parentCategoryNo = it.parentCategory!!.categoryNo!!,
                            categoryName = it.categoryName,
                            categoryDesc = it.categoryDesc,
                            categoryIcon = it.categoryIcon,
                            createdAt = it.createdAt,
                            modifiedAt = it.modifiedAt,
                        )
                    },
                parentCategoryNo = category.parentCategory?.categoryNo,
            )
        }
    }

    data class ChildCategory(
        var accountBookNo: Long,
        val categoryNo: Long,
        val parentCategoryNo: Long,
        val categoryName: String,
        val categoryDesc: String,
        val categoryIcon: String,
        val createdAt: ZonedDateTime,
        val modifiedAt: ZonedDateTime,
    )
}
