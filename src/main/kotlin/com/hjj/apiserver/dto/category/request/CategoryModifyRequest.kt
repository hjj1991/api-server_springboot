package com.hjj.apiserver.dto.category.request

import jakarta.validation.constraints.NotBlank

data class CategoryModifyRequest(
    val accountBookNo: Long,
    val parentCategoryNo: Long? = null,
    @field:NotBlank
    val categoryName: String,
    val categoryDesc: String,
    val categoryIcon: String,
)