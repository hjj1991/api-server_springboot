package com.hjj.apiserver.dto.accountbook.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.card.CardType
import com.hjj.apiserver.dto.category.CategoryDto
import java.time.LocalDateTime

data class AccountBookDetailResponse(
    var accountBookNo: Long,
    var accountBookName: String,
    var accountBookDesc: String,
    var accountRole: AccountRole = AccountRole.GUEST,
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var createdDate: LocalDateTime,
    var cards: List<CardDetail> = listOf(),
    var categories: List<CategoryDto> = listOf(),
) {
    data class CardDetail(
        var cardNo: Long,
        var cardName: String,
        var cardType: CardType,
        var cardDesc: String,
    ){

        companion object {
            fun of(card: Card): CardDetail{
                return CardDetail(
                    cardNo = card.cardNo!!,
                    cardName = card.cardName,
                    cardType = card.cardType,
                    cardDesc = card.cardDesc,
                )
            }
        }
    }

    class ChildrenCategory(
        var categoryNo: Long?,
        var categoryName: String?,
        var categoryIcon: String?,
        var parentCategoryNo: Long?,
    )


}