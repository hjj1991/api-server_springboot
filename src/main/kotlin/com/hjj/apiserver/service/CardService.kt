package com.hjj.apiserver.service

import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardModifyRequest
import com.hjj.apiserver.dto.card.response.CardFindAllResponse
import com.hjj.apiserver.repository.card.CardRepository
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CardService(
    private val cardRepository: CardRepository,
    private val modelMapper: ModelMapper,
) {

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun insertCard(user: User, request: CardAddRequest){
        cardRepository.save(Card(cardName = request.cardName, cardType = request.cardType, user = user))
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun deleteCard(userNo: Long, cardNo: Long){
        cardRepository.findByCardNoAndUser_UserNo(cardNo, userNo)?.delete()
            ?:throw IllegalArgumentException()
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun updateCard(userNo: Long, cardNo: Long, request: CardModifyRequest){
        cardRepository.findByCardNoAndUser_UserNo(cardNo = cardNo, userNo = userNo)?.updateCard(request.cardName, request.cardType, request.cardDesc)
            ?:throw IllegalArgumentException()
    }

    fun selectCards(userNo: Long):List<CardFindAllResponse>{
        val cards = cardRepository.findByUser_UserNoAndDeleteYn(userNo)
        return cards.map { CardFindAllResponse(it.cardNo!!, it.cardName, it.cardType, it.cardDesc) }
    }
}