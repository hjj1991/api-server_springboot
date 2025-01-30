package com.hjj.apiserver.repository.category

import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.dto.category.CategoryDto

interface CategoryRepositoryCustom {
    fun findCategories(
        userNo: Long,
        accountBookNo: Long,
    ): List<CategoryDto>

    fun findCategoryByAccountRole(
        categoryNo: Long,
        accountBookNo: Long,
        userNo: Long,
        accountRoles: Set<AccountRole> = setOf(AccountRole.OWNER, AccountRole.MEMBER),
    ): Category?

    fun findCategoryByCategoryNo(
        userNo: Long,
        categoryNo: Long,
    ): Category?
}
