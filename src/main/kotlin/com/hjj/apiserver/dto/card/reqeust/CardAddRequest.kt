package com.hjj.apiserver.dto.card.reqeust

import com.hjj.apiserver.domain.card.CardType

data class CardAddRequest(
    val cardName: String,
    val cardType: CardType,
    val cardDesc: String = "",
) {
}