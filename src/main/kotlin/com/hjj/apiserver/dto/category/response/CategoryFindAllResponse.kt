package com.hjj.apiserver.dto.category.response

import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.dto.category.CategoryDto

data class CategoryFindAllResponse(
    val categories: List<CategoryDto> = listOf(),
    val accountRole: AccountRole?,
) {
    companion object {
        fun of(
            categories: List<CategoryDto>,
            accountRole: AccountRole?,
        ): CategoryFindAllResponse {
            return CategoryFindAllResponse(
                categories = categories,
                accountRole = accountRole,
            )
        }
    }
}
