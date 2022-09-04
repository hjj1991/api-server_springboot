package com.hjj.apiserver.dto.card.response

import com.hjj.apiserver.domain.card.CardType

class CardFindAllResponse(
    val cardNo: Long,
    val cardName: String,
    val cardType: CardType,
    val cardDesc: String,
) {
}