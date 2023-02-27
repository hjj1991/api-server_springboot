package com.hjj.apiserver.dto.category

data class CategoryDto(
    val categoryNo: Long,
    val categoryName: String,
    val categoryDesc: String,
    val categoryIcon: String,
    val accountBookNo: Long,
    val childCategories: MutableList<ChildCategory> = mutableListOf(),
) {
    data class ChildCategory(
        val categoryNo: Long,
        val categoryName: String,
        val categoryDesc: String,
        val categoryIcon: String,
        val accountBookNo: Long,
        val parentCategoryNo: Long,
    )

}