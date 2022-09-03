package com.hjj.apiserver.service

import com.hjj.apiserver.domain.card.CardType
import com.hjj.apiserver.domain.purchase.PurchaseType
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseAddRequest
import com.hjj.apiserver.repository.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.persistence.EntityManager

@SpringBootTest
@Transactional
internal class PurchaseServiceTest @Autowired constructor(
    private val entityManager: EntityManager,
    private val purchaseService: PurchaseService,
    private val userRepository: UserRepository,
    private val cardService: CardService,
    private val accountBookService: AccountBookService,

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
    @DisplayName("들어온 등록이 정상 완료된다.")
    fun addPurchaseByIncome() {
        // given
        val savedUser = userRepository.save(
            User(
                userId = "testUser",
                nickName = "닉네임",
                userEmail = "tester@test.co.kr"
            )
        )

        val accountBookAddRequest = AccountBookAddRequest(
            "가게부",
            "설명",
            backGroundColor = "#ffffff",
            color = "#000000"
        )
        val savedAccountBook = accountBookService.addAccountBook(savedUser.userNo!!, accountBookAddRequest)


        // when
        val addPurchase = purchaseService.addPurchase(
            savedUser.userNo!!, PurchaseAddRequest(
                accountBookNo = savedAccountBook.accountBookNo!!,
                purchaseType = PurchaseType.INCOME,
                price = 100,
                reason = "월급",
                purchaseDate = LocalDate.now(),
            )
        )

        // then
        assertThat(addPurchase.purchaseType).isEqualTo(PurchaseType.INCOME)
        assertThat(addPurchase.price).isEqualTo(100)
        assertThat(addPurchase.reason).isEqualTo("월급")
        assertThat(addPurchase.purchaseDate).isEqualTo(LocalDate.now())

    }

    @Test
    @DisplayName("지출 등록이 정상 완료된다.")
    fun addPurchaseByOutgoing() {
        // given
        val savedUser = userRepository.save(
            User(
                userId = "testUser",
                nickName = "닉네임",
                userEmail = "tester@test.co.kr"
            )
        )

        val accountBookAddRequest = AccountBookAddRequest(
            "가게부",
            "설명",
            backGroundColor = "#ffffff",
            color = "#000000"
        )
        val savedAccountBook = accountBookService.addAccountBook(savedUser.userNo!!, accountBookAddRequest)
        val insertCard = cardService.insertCard(savedUser.userNo!!, CardAddRequest(
            cardName = "테스트카드",
            cardType = CardType.CHECK_CARD,
            cardDesc = "카드설명",
        ))




        // when
        /* 현금인 경우 */
        val addPurchaseByCash = purchaseService.addPurchase(
            savedUser.userNo!!, PurchaseAddRequest(
                accountBookNo = savedAccountBook.accountBookNo!!,
                purchaseType = PurchaseType.OUTGOING,
                price = 100,
                reason = "껌값",
                purchaseDate = LocalDate.now(),
            )
        )

        /* 카드가 있는 경우 */
        val addPurchaseByCard = purchaseService.addPurchase(
            savedUser.userNo!!, PurchaseAddRequest(
                accountBookNo = savedAccountBook.accountBookNo!!,
                purchaseType = PurchaseType.OUTGOING,
                cardNo = insertCard.cardNo,
                price = 100,
                reason = "껌값",
                purchaseDate = LocalDate.now(),
            )
        )

        // then
        /* 현금인 경우 */
        assertThat(addPurchaseByCash.purchaseType).isEqualTo(PurchaseType.OUTGOING)
        assertThat(addPurchaseByCash.price).isEqualTo(100)
        assertThat(addPurchaseByCash.reason).isEqualTo("껌값")
        assertThat(addPurchaseByCash.purchaseDate).isEqualTo(LocalDate.now())

        /* 카드가 있는 경우 */
        assertThat(addPurchaseByCard.purchaseType).isEqualTo(PurchaseType.OUTGOING)
        assertThat(addPurchaseByCard.price).isEqualTo(100)
        assertThat(addPurchaseByCard.card).isNotNull
        assertThat(addPurchaseByCard.reason).isEqualTo("껌값")
        assertThat(addPurchaseByCard.purchaseDate).isEqualTo(LocalDate.now())

    }

    @Test
    fun findPurchasesOfPage() {
        // given
        val savedUser = userRepository.save(
            User(
                userId = "testUser",
                nickName = "닉네임",
                userEmail = "tester@test.co.kr"
            )
        )

        val accountBookAddRequest = AccountBookAddRequest(
            "가게부",
            "설명",
            backGroundColor = "#ffffff",
            color = "#000000"
        )
        val savedAccountBook = accountBookService.addAccountBook(savedUser.userNo!!, accountBookAddRequest)
        val insertCard = cardService.insertCard(savedUser.userNo!!, CardAddRequest(
            cardName = "테스트카드",
            cardType = CardType.CHECK_CARD,
            cardDesc = "카드설명",
        ))




        // when
        /* 현금인 경우 */
        val addPurchaseByCash = purchaseService.addPurchase(
            savedUser.userNo!!, PurchaseAddRequest(
                accountBookNo = savedAccountBook.accountBookNo!!,
                purchaseType = PurchaseType.OUTGOING,
                price = 100,
                reason = "껌값",
                purchaseDate = LocalDate.now(),
            )
        )

        /* 카드가 있는 경우 */
        val addPurchaseByCard = purchaseService.addPurchase(
            savedUser.userNo!!, PurchaseAddRequest(
                accountBookNo = savedAccountBook.accountBookNo!!,
                purchaseType = PurchaseType.OUTGOING,
                cardNo = insertCard.cardNo,
                price = 100,
                reason = "껌값",
                purchaseDate = LocalDate.now(),
            )
        )

    }

    @Test
    fun deletePurchase() {
    }

    @Test
    fun modifyPurchase() {
    }

    @Test
    fun findPurchase() {
    }
}