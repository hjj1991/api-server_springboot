package com.hjj.apiserver.controller

import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardModifyRequest
import com.hjj.apiserver.dto.card.response.CardAddResponse
import com.hjj.apiserver.dto.card.response.CardFindAllResponse
import com.hjj.apiserver.dto.card.response.CardFindResponse
import com.hjj.apiserver.dto.card.response.CardModifyResponse
import com.hjj.apiserver.dto.user.CurrentUserInfo
import com.hjj.apiserver.service.CardService
import com.hjj.apiserver.util.CurrentUser
import io.swagger.annotations.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class CardController(
    private val cardService: CardService,
) {

    @GetMapping("/card")
    fun cardsFind(@CurrentUser currentUserInfo: CurrentUserInfo): List<CardFindAllResponse> {
        return cardService.findCards(currentUserInfo.userNo)
    }

    @PostMapping("/card")
    @ResponseStatus(HttpStatus.CREATED)
    fun cardAdd(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @RequestBody @Valid request: CardAddRequest
    ): CardAddResponse {
        return cardService.addCard(currentUserInfo.userNo, request)
    }

    @DeleteMapping("/card/{cardNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cardRemove(
        @CurrentUser currentUserInfo: CurrentUserInfo, @PathVariable("cardNo") cardNo: Long
    ) {
        cardService.removeCard(currentUserInfo.userNo, cardNo)
    }

    @PutMapping("/card/{cardNo}")
    fun cardModify(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @PathVariable("cardNo") cardNo: Long,
        @RequestBody @Valid request: CardModifyRequest
    ): CardModifyResponse {
        return cardService.modifyCard(currentUserInfo.userNo, cardNo, request)
    }

    @GetMapping("/card/{cardNo}")
    fun cardDetail(
        @CurrentUser currentUserInfo: CurrentUserInfo,
        @PathVariable("cardNo") cardNo: Long
    ): CardFindResponse {
        return cardService.findCardDetail(currentUserInfo.userNo, cardNo)
    }

}