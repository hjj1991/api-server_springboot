package com.hjj.apiserver.dto.category.request

import jakarta.validation.constraints.NotEmpty

data class CategoryAddRequest(
    val accountBookNo: Long,
    val parentCategoryNo: Long? = null,
    @field:NotEmpty
    val categoryName: String,
    val categoryDesc: String,
    val categoryIcon: String,
) {
}