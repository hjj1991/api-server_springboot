package com.hjj.apiserver.dto.category.response

import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.dto.category.CategoryDto

class CategoryFindAllResponse(
    val categories: List<CategoryDto> = mutableListOf(),
    val accountRole: AccountRole?,
) {
}