package com.hjj.apiserver.dto.accountbook.response

import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.card.CardType
import com.hjj.apiserver.dto.category.CategoryDto
import java.time.LocalDateTime

data class AccountBookDetailResponse(
    var accountBookNo: Long,
    var accountBookName: String,
    var accountBookDesc: String,
    var accountRole: AccountRole = AccountRole.GUEST,
    var createdDate: LocalDateTime,
    var cards: List<CardDetail> = listOf(),
    var categories: List<CategoryDto> = listOf(),
) {
    class CardDetail(
        var cardNo: Long,
        var cardName: String,
        var cardType: CardType,
    )

    class ChildrenCategory(
        var categoryNo: Long?,
        var categoryName: String?,
        var categoryIcon: String?,
        var parentCategoryNo: Long?,
    )
}