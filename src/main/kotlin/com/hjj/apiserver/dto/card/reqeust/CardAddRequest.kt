package com.hjj.apiserver.dto.card.reqeust

import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.card.CardType
import com.hjj.apiserver.domain.user.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CardAddRequest(
    @field:NotBlank
    val cardName: String,
    @field:NotNull
    val cardType: CardType,
    @field:NotNull
    val cardDesc: String = "",
) {

    fun toEntity(user: User): Card {
        return Card(
            cardName = cardName,
            cardType = cardType,
            cardDesc = cardDesc,
            user = user,
        )
    }
}