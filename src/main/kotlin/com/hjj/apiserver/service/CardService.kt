package com.hjj.apiserver.service

import com.hjj.apiserver.common.exception.CardNotFoundException
import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardModifyRequest
import com.hjj.apiserver.dto.card.response.CardAddResponse
import com.hjj.apiserver.dto.card.response.CardFindAllResponse
import com.hjj.apiserver.dto.card.response.CardFindResponse
import com.hjj.apiserver.dto.card.response.CardModifyResponse
import com.hjj.apiserver.repository.card.CardRepository
import com.hjj.apiserver.repository.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CardService(
    private val cardRepository: CardRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = false)
    fun addCard(userNo: Long, request: CardAddRequest): CardAddResponse {
        val userByLazy = userRepository.getReferenceById(userNo)
        val card = request.toEntity(userByLazy)
        cardRepository.save(card)
        return CardAddResponse.of(card)
    }

    @Transactional(readOnly = false)
    fun removeCard(userNo: Long, cardNo: Long) {
        cardRepository.findByCardNoAndUser_UserNo(cardNo, userNo)?.delete() ?: throw CardNotFoundException()
    }

    @Transactional(readOnly = false)
    fun modifyCard(userNo: Long, cardNo: Long, request: CardModifyRequest): CardModifyResponse {
        val foundCard =
            cardRepository.findByCardNoAndUser_UserNo(cardNo = cardNo, userNo = userNo) ?: throw CardNotFoundException()

        foundCard.updateCard(request.cardName, request.cardType, request.cardDesc)

        return CardModifyResponse.of(foundCard)
    }

    fun findCards(userNo: Long): List<CardFindAllResponse> {
        return cardRepository.findByUser_UserNoAndDeleteYn(userNo)
            .map { CardFindAllResponse(it.cardNo!!, it.cardName, it.cardType, it.cardDesc) }
    }

    fun findCardDetail(userNo: Long, cardNo: Long): CardFindResponse {
        val foundCard = cardRepository.findByCardNoAndUser_UserNo(cardNo, userNo) ?: throw CardNotFoundException()
        return CardFindResponse.of(foundCard)
    }
}