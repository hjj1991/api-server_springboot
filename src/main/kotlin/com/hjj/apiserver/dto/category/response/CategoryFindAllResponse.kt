package com.hjj.apiserver.dto.category.response

class CategoryFindAllResponse(
    val categoryNo: Long,
    val categoryName: String,
    val categoryDesc: String,
    val categoryIcon: String,
    val accountBookNo: Long,
    val childCategories: MutableList<ChildCategory> = mutableListOf(),
) {

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