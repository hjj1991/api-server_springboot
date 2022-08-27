package com.hjj.apiserver.dto.category.request

import javax.validation.constraints.NotEmpty

data class CategoryAddRequest(
    val accountBookNo: Long,
    val parentCategoryNo: Long? = null,
    @NotEmpty
    val categoryName: String,
    val categoryDesc: String,
    val categoryIcon: String,
) {
}