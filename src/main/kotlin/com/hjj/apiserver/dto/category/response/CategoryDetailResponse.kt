package com.hjj.apiserver.dto.category.response

import java.time.LocalDateTime


class CategoryDetailResponse(
    val accountBookNo: Long,
    val categoryNo: Long,
    val parentCategoryNo: Long? = null,
    val categoryName: String,
    val categoryDesc: String,
    val categoryIcon: String,
    val childCategories: List<ChildCategory>
) {

    class ChildCategory(
        var accountBookNo: Long,
        val categoryNo: Long,
        val parentCategoryNo: Long,
        val categoryName: String,
        val categoryDesc: String,
        val categoryIcon: String,
        val createdDate: LocalDateTime,
        val lastModifiedDate: LocalDateTime,
    )
}