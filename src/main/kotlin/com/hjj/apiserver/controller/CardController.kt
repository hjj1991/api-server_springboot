package com.hjj.apiserver.controller

import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardModifyRequest
import com.hjj.apiserver.dto.card.response.CardAddResponse
import com.hjj.apiserver.dto.card.response.CardFindAllResponse
import com.hjj.apiserver.dto.card.response.CardFindResponse
import com.hjj.apiserver.dto.card.response.CardModifyResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.impl.CardService
import com.hjj.apiserver.util.AuthUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class CardController(
    private val cardService: CardService,
) {

    @GetMapping("/cards")
    fun cardsFind(@AuthUser authUserInfo: CurrentUserInfo): List<CardFindAllResponse> {
        return cardService.findCards(authUserInfo.userNo)
    }

    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    fun cardAdd(
        @AuthUser authUserInfo: CurrentUserInfo,
        @RequestBody @Valid request: CardAddRequest
    ): CardAddResponse {
        return cardService.addCard(authUserInfo.userNo, request)
    }

    @DeleteMapping("/cards/{cardNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cardRemove(
        @AuthUser authUserInfo: CurrentUserInfo, @PathVariable("cardNo") cardNo: Long
    ) {
        cardService.removeCard(authUserInfo.userNo, cardNo)
    }

    @PutMapping("/cards/{cardNo}")
    fun cardModify(
        @AuthUser authUserInfo: CurrentUserInfo,
        @PathVariable("cardNo") cardNo: Long,
        @RequestBody @Valid request: CardModifyRequest
    ): CardModifyResponse {
        return cardService.modifyCard(authUserInfo.userNo, cardNo, request)
    }

    @GetMapping("/cards/{cardNo}")
    fun cardDetail(
        @AuthUser authUserInfo: CurrentUserInfo,
        @PathVariable("cardNo") cardNo: Long
    ): CardFindResponse {
        return cardService.findCardDetail(authUserInfo.userNo, cardNo)
    }

}