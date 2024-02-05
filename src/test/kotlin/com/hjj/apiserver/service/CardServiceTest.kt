package com.hjj.apiserver.service

import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
import com.hjj.apiserver.adapter.out.persistence.user.UserRepository
import com.hjj.apiserver.common.exception.CardNotFoundException
import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.card.CardType
import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardModifyRequest
import com.hjj.apiserver.dto.card.response.CardFindAllResponse
import com.hjj.apiserver.repository.card.CardRepository
import com.hjj.apiserver.service.impl.CardService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CardServiceTest {

    @InjectMocks
    lateinit var cardService: CardService

    @Mock
    lateinit var cardRepository: CardRepository

    @Mock
    lateinit var userRepository: UserRepository


    @DisplayName("카드가 정상 생성된다.")
    @Test
    fun addCard_success() {
        // Given
        val cardAddRequest = CardAddRequest(
            cardName = "신한카드",
            cardType = CardType.CREDIT_CARD,
            cardDesc = "신한 신용카드"
        )
        val savedUserEntity = UserEntity(
            userNo = 1L,
            nickName = "닉네임",
            userEmail = "tester@test.co.kr"
        )
        val newCard = Card(
            cardNo = 1L,
            cardName = cardAddRequest.cardName,
            cardType = cardAddRequest.cardType,
            cardDesc = cardAddRequest.cardDesc,
            userEntity = savedUserEntity
        )

        Mockito.`when`(cardRepository.save(Mockito.any()))
            .thenReturn(newCard)

        Mockito.`when`(userRepository.getReferenceById(1L))
            .thenReturn(savedUserEntity)

        // When
        val cardAddResponse = cardService.addCard(savedUserEntity.userNo!!, cardAddRequest)


        // Then
        assertThat(cardAddResponse.cardNo).isEqualTo(newCard.cardNo)
        assertThat(cardAddResponse.cardDesc).isEqualTo(cardAddRequest.cardDesc)
        assertThat(cardAddResponse.cardName).isEqualTo(cardAddRequest.cardName)
        assertThat(cardAddResponse.cardType).isEqualTo(cardAddRequest.cardType)
    }

    @DisplayName("카드가 정상 삭제된다.")
    @Test
    fun removeCard_success() {
        // Given
        val savedUserEntity = UserEntity(
            userNo = 1L,
            nickName = "닉네임",
            userEmail = "tester@test.co.kr"
        )
        val newCard = Card(
            cardNo = 1L,
            cardName = "신한카드",
            cardType = CardType.CREDIT_CARD,
            cardDesc = "신한 신용카드",
            userEntity = savedUserEntity
        )

        Mockito.`when`(cardRepository.findByCardNoAndUserEntity_UserNoAndIsDeleteIsFalse(newCard.cardNo!!, savedUserEntity.userNo!!))
            .thenReturn(newCard)


        // When
        cardService.removeCard(savedUserEntity.userNo!!, newCard.cardNo!!)

        // Then
        assertThat(newCard.isDelete).isTrue()
    }

    @DisplayName("카드 삭제시 카드가 존재하지 않으면 CardNotFoundException가 발생한다.")
    @Test
    fun removeCard_fail_notExistsCard_raise_CardNotFoundException() {
        // Given
        val cardNo = 1L
        val userNo = 1L

        Mockito.`when`(cardRepository.findByCardNoAndUserEntity_UserNoAndIsDeleteIsFalse(cardNo, userNo))
            .thenReturn(null)

        // When && Then
        assertThatThrownBy { cardService.removeCard(userNo, cardNo) }
            .isInstanceOf(CardNotFoundException::class.java)
    }

    @DisplayName("카드정보가 정상 수정된다.")
    @Test
    fun modifyCard_success() {
        // Given
        val savedUserEntity = UserEntity(
            userNo = 1L,
            nickName = "닉네임",
            userEmail = "tester@test.co.kr"
        )
        val savedCard = Card(
            cardNo = 1L,
            cardName = "신한카드",
            cardType = CardType.CREDIT_CARD,
            cardDesc = "신한 신용카드",
            userEntity = savedUserEntity
        )

        val cardModifyRequest = CardModifyRequest(
            cardName = "KB카드",
            cardType = CardType.CHECK_CARD,
            cardDesc = "KB 체크카드"
        )

        Mockito.`when`(
            cardRepository.findByCardNoAndUserEntity_UserNoAndIsDeleteIsFalse(
                savedCard.cardNo!!,
                savedUserEntity.userNo!!
            )
        )
            .thenReturn(savedCard)

        // When
        val cardModifyResponse = cardService.modifyCard(savedUserEntity.userNo!!, savedCard.cardNo!!, cardModifyRequest)

        // Then
        assertThat(cardModifyResponse.cardNo).isEqualTo(savedCard.cardNo)
        assertThat(cardModifyResponse.cardName).isEqualTo(cardModifyRequest.cardName)
        assertThat(cardModifyResponse.cardType).isEqualTo(cardModifyRequest.cardType)
        assertThat(cardModifyResponse.cardDesc).isEqualTo(cardModifyRequest.cardDesc)
    }

    @DisplayName("카드 수정시 카드가 존재하지 않으면 CardNotFoundException가 발생한다.")
    @Test
    fun modifyCard_fail_notExistsCard_raise_CardNotFoundException() {
        // Given
        val cardNo = 1L
        val userNo = 1L
        val cardModifyRequest = CardModifyRequest(
            cardName = "KB카드",
            cardType = CardType.CHECK_CARD,
            cardDesc = "KB 체크카드"
        )


        Mockito.`when`(cardRepository.findByCardNoAndUserEntity_UserNoAndIsDeleteIsFalse(cardNo, userNo))
            .thenReturn(null)

        // When && Then
        assertThatThrownBy { cardService.modifyCard(userNo, cardNo, cardModifyRequest) }
            .isInstanceOf(CardNotFoundException::class.java)
    }


    @DisplayName("사용자 카드 전체 조회가 성공한다.")
    @Test
    fun findCards_success() {
        // Given
        val savedUserEntity = UserEntity(
            userNo = 1L,
            nickName = "닉네임",
            userEmail = "tester@test.co.kr"
        )
        val cards = listOf(
            Card(1L, "국민카드", CardType.CHECK_CARD, "KB카드 설명", savedUserEntity),
            Card(2L, "신한카드", CardType.CREDIT_CARD, "신한카드 설명", savedUserEntity)
        )

        Mockito.`when`(cardRepository.findByUserEntity_UserNoAndIsDeleteIsFalse(savedUserEntity.userNo!!))
            .thenReturn(cards.toMutableList())

        // When
        val cardFindAllResponses = cardService.findCards(savedUserEntity.userNo!!)

        // Then
        assertThat(cardFindAllResponses.size).isEqualTo(2)
        assertThat(cardFindAllResponses)
            .map(
                CardFindAllResponse::cardNo,
                CardFindAllResponse::cardName,
                CardFindAllResponse::cardType,
                CardFindAllResponse::cardDesc
            )
            .contains(
                Tuple.tuple(cards[0].cardNo, cards[0].cardName, cards[0].cardType, cards[0].cardDesc),
                Tuple.tuple(cards[1].cardNo, cards[1].cardName, cards[1].cardType, cards[1].cardDesc),
            )
    }

    @DisplayName("카드 상세조회가 정상 조회된다.")
    @Test
    fun findCardDetail_success() {
        // Given
        val savedUserEntity = UserEntity(
            userNo = 1L,
            nickName = "닉네임",
            userEmail = "tester@test.co.kr"
        )
        val savedCard = Card(
            cardNo = 1L,
            cardName = "신한카드",
            cardType = CardType.CREDIT_CARD,
            cardDesc = "신한 신용카드",
            userEntity = savedUserEntity
        )

        Mockito.`when`(
            cardRepository.findByCardNoAndUserEntity_UserNoAndIsDeleteIsFalse(
                savedCard.cardNo!!,
                savedUserEntity.userNo!!
            )
        ).thenReturn(savedCard)

        // When
        val findCardDetail = cardService.findCardDetail(savedUserEntity.userNo!!, savedCard.cardNo!!)

        // Then
        assertThat(findCardDetail.cardNo).isEqualTo(savedCard.cardNo)
        assertThat(findCardDetail.cardName).isEqualTo(savedCard.cardName)
        assertThat(findCardDetail.cardDesc).isEqualTo(savedCard.cardDesc)
        assertThat(findCardDetail.cardType).isEqualTo(savedCard.cardType)
    }

    @DisplayName("카드 상세조회시 카드가 존재하지 않으면 CardNotFoundException가 발생한다.")
    @Test
    fun findCardDetail_fail_when_cardNotExists_raise_cardNotFoundException(){
        //Given
        val userNo = 1L
        val cardNo = 1L

        Mockito.`when`(cardRepository.findByCardNoAndUserEntity_UserNoAndIsDeleteIsFalse(cardNo, userNo))
            .thenReturn(null)

        // When && Then
        assertThatThrownBy { cardService.findCardDetail(userNo, cardNo) }
            .isInstanceOf(CardNotFoundException::class.java)
    }
}