package com.hjj.apiserver.service

import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardModifyRequest
import com.hjj.apiserver.dto.card.response.CardFindAllResponse
import com.hjj.apiserver.dto.card.response.CardFindResponse
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

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun insertCard(userNo: Long, request: CardAddRequest): Card {
        return cardRepository.save(
            Card(
                cardName = request.cardName,
                cardType = request.cardType,
                cardDesc = request.cardDesc,
                user = userRepository.getById(userNo)
            )
        )
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun deleteCard(userNo: Long, cardNo: Long) {
        cardRepository.findByCardNoAndUser_UserNo(cardNo, userNo)?.delete()
            ?: throw IllegalArgumentException()
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun updateCard(userNo: Long, cardNo: Long, request: CardModifyRequest) {
        cardRepository.findByCardNoAndUser_UserNo(cardNo = cardNo, userNo = userNo)
            ?.updateCard(request.cardName, request.cardType, request.cardDesc)
            ?: throw IllegalArgumentException()
    }

    fun selectCards(userNo: Long): List<CardFindAllResponse> {
        return cardRepository.findByUser_UserNoAndDeleteYn(userNo)
            .map { CardFindAllResponse(it.cardNo!!, it.cardName, it.cardType, it.cardDesc) }
    }

    fun selectCard(userNo: Long, cardNo: Long): CardFindResponse {
        return cardRepository.findByCardNoAndUser_UserNo(cardNo, userNo)
            ?.run { CardFindResponse(cardNo, cardName, cardType, cardDesc) }
            ?: throw IllegalArgumentException()
    }
}