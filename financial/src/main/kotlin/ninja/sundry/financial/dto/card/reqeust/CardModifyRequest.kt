package com.hjj.apiserver.dto.card.reqeust

import com.hjj.apiserver.domain.card.CardType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CardModifyRequest(
    @field:NotBlank
    val cardName: String,
    @field:NotNull
    val cardType: CardType,
    @field:NotNull
    val cardDesc: String = "",
)
