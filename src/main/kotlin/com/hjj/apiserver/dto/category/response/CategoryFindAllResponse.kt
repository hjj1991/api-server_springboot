package com.hjj.apiserver.dto.category.response

import com.hjj.apiserver.domain.accountbook.AccountRole

class CategoryFindAllResponse(
    val categories: List<Categories> = mutableListOf(),
    val accountRole: AccountRole?,
) {
    class Categories(
        val categoryNo: Long,
        val categoryName: String,
        val categoryDesc: String,
        val categoryIcon: String,
        val accountBookNo: Long,
        val childCategories: MutableList<ChildCategory> = mutableListOf(),
    )

    class ChildCategory(
        val categoryNo: Long,
        val categoryName: String,
        val categoryDesc: String,
        val categoryIcon: String,
        val accountBookNo: Long,
        val parentCategoryNo: Long,
    ){

    }
}