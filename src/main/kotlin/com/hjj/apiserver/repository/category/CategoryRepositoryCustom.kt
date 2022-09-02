package com.hjj.apiserver.repository.category

import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.dto.category.response.CategoryFindAllResponse

interface CategoryRepositoryCustom {
    fun findCategories(userNo: Long, accountBookNo: Long): List<CategoryFindAllResponse>
    fun findCategoryByOwner(categoryNo: Long, accountBookNo: Long, userNo: Long): Category?
}