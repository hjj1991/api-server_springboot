package com.hjj.apiserver.service

import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.card.CardType
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardModifyRequest
import com.hjj.apiserver.repository.card.CardRepository
import com.hjj.apiserver.repository.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@SpringBootTest
@Transactional
internal class CardServiceTest @Autowired constructor(
    private val cardService: CardService,
    private val userRepository: UserRepository,
    private val cardRepository: CardRepository,
    private val entityManager: EntityManager,
) {

    @BeforeEach
    fun clean(){
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE; " +
                "TRUNCATE TABLE tb_category; " +
                "TRUNCATE TABLE tb_account_book_user; " +
                "TRUNCATE TABLE tb_account_book; " +
                "TRUNCATE TABLE tb_purchase; " +
                "TRUNCATE TABLE tb_user; " +
                "TRUNCATE TABLE tb_card; " +
                "SET REFERENTIAL_INTEGRITY TRUE; ").executeUpdate()
    }

    @Test
    @DisplayName("카드가 정상적으로 추가된다.")
    fun insertCardTest() {
        // given
        val savedUser = userRepository.save(
            User(
                userId = "testUser",
                nickName = "닉네임",
                userEmail = "tester@test.co.kr"
            )
        )
        val cardAddRequest = CardAddRequest(
            cardName = "테스트카드",
            cardType = CardType.CHECK_CARD,
            cardDesc = "카드설명",
        )


        // when
        val insertCard = cardService.findCard(savedUser.userNo!!, cardAddRequest)

        // then
        assertThat(insertCard.cardName).isEqualTo("테스트카드")
        assertThat(insertCard.cardDesc).isEqualTo("카드설명")
        assertThat(insertCard.cardType).isEqualTo(CardType.CHECK_CARD)

    }

    @Test
    @DisplayName("카드가 정상적으로 삭제된다.")
    fun deleteCardTest() {
        // given
        val savedUser = userRepository.save(
            User(
                userId = "testUser",
                nickName = "닉네임",
                userEmail = "tester@test.co.kr"
            )
        )
        val cardAddRequest = CardAddRequest(
            cardName = "테스트카드",
            cardType = CardType.CHECK_CARD,
            cardDesc = "카드설명",
        )
        val insertCard = cardService.findCard(savedUser.userNo!!, cardAddRequest)

        // when
        cardService.removeCard(savedUser.userNo!!, insertCard.cardNo!!)

        // then
        val card = cardRepository.findByIdOrNull(insertCard.cardNo) ?: throw IllegalStateException()
        assertThat(card.deleteYn).isEqualTo('Y')

    }

    @Test
    @DisplayName("카드가 정상적으로 수정된다.")
    fun updateCardTest() {
        // given
        val savedUser = userRepository.save(
            User(
                userId = "testUser",
                nickName = "닉네임",
                userEmail = "tester@test.co.kr"
            )
        )
        val cardAddRequest = CardAddRequest(
            cardName = "테스트카드",
            cardType = CardType.CHECK_CARD,
            cardDesc = "카드설명",
        )
        val insertCard = cardService.findCard(savedUser.userNo!!, cardAddRequest)

        val cardModifyRequest = CardModifyRequest(
            cardName = "변경한카드명",
            cardType = CardType.CREDIT_CARD,
            cardDesc = "변경한설명",
        )

        // when
        cardService.modifyCard(savedUser.userNo!!, insertCard.cardNo!!, cardModifyRequest)

        // then
        val card = cardRepository.findByIdOrNull(insertCard.cardNo) ?: throw IllegalStateException()
        assertThat(card.cardName).isEqualTo("변경한카드명")
        assertThat(card.cardType).isEqualTo(CardType.CREDIT_CARD)
        assertThat(card.cardDesc).isEqualTo("변경한설명")
    }

    @Test
    @DisplayName("카드목록이 한번에 정상적으로 조회된다.")
    fun selectCardsTest() {
        // given
        val savedUser = userRepository.save(
            User(
                userId = "testUser",
                nickName = "닉네임",
                userEmail = "tester@test.co.kr"
            )
        )
        val cards = mutableListOf<Card>()
        for (i in 0..10) {
            cards.add(
                Card(
                    cardName = "테스트카드${i}",
                    cardType = CardType.CHECK_CARD,
                    cardDesc = "카드설명${i}",
                    user = savedUser,
                )
            )
        }

        cardRepository.saveAll(cards)
        // when
        val selectCards = cardService.findCards(savedUser.userNo!!)

        // then
        assertThat(selectCards).hasSize(11)
        assertThat(selectCards).extracting("cardName")
            .contains("테스트카드0", "테스트카드1", "테스트카드10")
        assertThat(selectCards).extracting("cardDesc")
            .contains("카드설명0", "카드설명10")


    }

    @Test
    @DisplayName("카드의 상세정보가 조회된다.")
    fun selectCard() {
        // given
        val savedUser = userRepository.save(
            User(
                userId = "testUser",
                nickName = "닉네임",
                userEmail = "tester@test.co.kr"
            )
        )
        val cardAddRequest = CardAddRequest(
            cardName = "테스트카드",
            cardType = CardType.CHECK_CARD,
            cardDesc = "카드설명",
        )
        val insertCard = cardService.findCard(savedUser.userNo!!, cardAddRequest)

        // when
        val selectCard = cardService.findCard(savedUser.userNo!!, insertCard.cardNo!!)

        // then

        assertThat(selectCard.cardName).isEqualTo("테스트카드")
        assertThat(selectCard.cardType).isEqualTo(CardType.CHECK_CARD)
        assertThat(selectCard.cardDesc).isEqualTo("카드설명")
    }
}