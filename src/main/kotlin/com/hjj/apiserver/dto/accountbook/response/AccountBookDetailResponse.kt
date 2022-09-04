package com.hjj.apiserver.dto.accountbook.response

import com.hjj.apiserver.domain.accountbook.AccountRole
import java.time.LocalDateTime

class AccountBookDetailResponse(
    var accountBookNo: Long,
    var accountBookName: String,
    var accountBookDesc: String,
    var accountRole: AccountRole = AccountRole.GUEST,
    var createdDate: LocalDateTime,
    var cards: List<CardDetail> = listOf(),
    var categories: List<CategoryDetail> = listOf(),
) {
    class CardDetail(
        var cardNo: Long,
        var cardName: String,
    )

    class CategoryDetail(
        var categoryNo: Long?,
        var categoryName: String?,
        var categoryIcon: String?,
        var childCategories: List<ChildrenCategory>? = mutableListOf()
    )

    class ChildrenCategory(
        var categoryNo: Long?,
        var categoryName: String?,
        var categoryIcon: String?,
        var parentCategoryNo: Long?,
    )
}