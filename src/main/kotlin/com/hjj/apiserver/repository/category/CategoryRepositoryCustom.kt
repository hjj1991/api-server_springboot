package com.hjj.apiserver.repository.category

import com.hjj.apiserver.dto.category.response.CategoryFindAllResponse

interface CategoryRepositoryCustom {
    fun findCategories(userNo: Long, accountBookNo: Long): List<CategoryFindAllResponse>
}