package com.hjj.apiserver.dto.card.response

import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.card.CardType

data class CardModifyResponse(
    var cardNo: Long,
    val cardName: String,
    val cardType: CardType,
    val cardDesc: String,
) {

    companion object{
        fun of(card: Card):CardModifyResponse {
            return CardModifyResponse(
                cardNo = card.cardNo!!,
                cardName = card.cardName,
                cardType = card.cardType,
                cardDesc = card.cardDesc
            )

        }
    }
}