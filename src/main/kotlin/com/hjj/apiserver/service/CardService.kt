package com.hjj.apiserver.service

import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardModifyRequest
import com.hjj.apiserver.dto.card.response.CardAddResponse
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
    fun addCard(userNo: Long, request: CardAddRequest): CardAddResponse {
        val userByLazy = userRepository.getReferenceById(userNo)
        val card = request.toEntity(userByLazy)
        cardRepository.save(card)
        return CardAddResponse.of(card)
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun removeCard(userNo: Long, cardNo: Long) {
        cardRepository.findByCardNoAndUser_UserNo(cardNo, userNo)?.delete()
            ?: throw IllegalArgumentException()
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun modifyCard(userNo: Long, cardNo: Long, request: CardModifyRequest) {
        cardRepository.findByCardNoAndUser_UserNo(cardNo = cardNo, userNo = userNo)
            ?.updateCard(request.cardName, request.cardType, request.cardDesc)
            ?: throw IllegalArgumentException()
    }

    fun findCards(userNo: Long): List<CardFindAllResponse> {
        return cardRepository.findByUser_UserNoAndDeleteYn(userNo)
            .map { CardFindAllResponse(it.cardNo!!, it.cardName, it.cardType, it.cardDesc) }
    }

    fun findCardDetail(userNo: Long, cardNo: Long): CardFindResponse {
        return cardRepository.findByCardNoAndUser_UserNo(cardNo, userNo)
            ?.run { CardFindResponse(cardNo, cardName, cardType, cardDesc) }
            ?: throw IllegalArgumentException()
    }
}