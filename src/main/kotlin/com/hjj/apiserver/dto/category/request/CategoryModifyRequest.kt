package com.hjj.apiserver.dto.category.request

class CategoryModifyRequest(
    val accountBookNo: Long,
    val parentCategoryNo: Long? = null,
    val categoryName: String,
    val categoryDesc: String,
    val categoryIcon: String,
)