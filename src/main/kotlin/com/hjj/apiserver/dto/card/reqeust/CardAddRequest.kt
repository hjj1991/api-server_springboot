package com.hjj.apiserver.dto.card.reqeust

import com.hjj.apiserver.adapter.out.persistence.user.entity.UserEntity
import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.card.CardType
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
    fun toEntity(userEntity: UserEntity): Card {
        return Card(
            cardName = cardName,
            cardType = cardType,
            cardDesc = cardDesc,
            userEntity = userEntity,
        )
    }
}
