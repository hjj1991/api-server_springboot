package com.hjj.apiserver.service.impl

import com.hjj.apiserver.adapter.out.persistence.user.repository.UserRepository
import com.hjj.apiserver.common.exception.CardNotFoundException
import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardModifyRequest
import com.hjj.apiserver.dto.card.response.CardAddResponse
import com.hjj.apiserver.dto.card.response.CardFindAllResponse
import com.hjj.apiserver.dto.card.response.CardFindResponse
import com.hjj.apiserver.dto.card.response.CardModifyResponse
import com.hjj.apiserver.repository.card.CardRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CardService(
    private val cardRepository: CardRepository,
    private val userRepository: UserRepository,
) {
    @Transactional(readOnly = false)
    fun addCard(
        userNo: Long,
        request: CardAddRequest,
    ): CardAddResponse {
        val userByLazy = userRepository.getReferenceById(userNo)
        val savedCard = cardRepository.save(request.toEntity(userByLazy))
        return CardAddResponse.of(savedCard)
    }

    @Transactional(readOnly = false)
    fun removeCard(
        userNo: Long,
        cardNo: Long,
    ) {
        val card =
            cardRepository.findByCardNoAndUserEntityUserNoAndIsDeleteIsFalse(cardNo, userNo) ?: throw CardNotFoundException()
        card.delete()
    }

    @Transactional(readOnly = false)
    fun modifyCard(
        userNo: Long,
        cardNo: Long,
        request: CardModifyRequest,
    ): CardModifyResponse {
        val foundCard =
            cardRepository.findByCardNoAndUserEntityUserNoAndIsDeleteIsFalse(cardNo = cardNo, userNo = userNo)
                ?: throw CardNotFoundException()

        foundCard.updateCard(request.cardName, request.cardType, request.cardDesc)

        return CardModifyResponse.of(foundCard)
    }

    fun findCards(userNo: Long): List<CardFindAllResponse> {
        return cardRepository.findByUserEntityUserNoAndIsDeleteIsFalse(userNo).map(CardFindAllResponse::of)
    }

    fun findCardDetail(
        userNo: Long,
        cardNo: Long,
    ): CardFindResponse {
        val foundCard =
            cardRepository.findByCardNoAndUserEntityUserNoAndIsDeleteIsFalse(cardNo, userNo)
                ?: throw CardNotFoundException()
        return CardFindResponse.of(foundCard)
    }
}
