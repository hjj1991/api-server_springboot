package com.hjj.apiserver.service

import com.hjj.apiserver.domain.card.CardType
import com.hjj.apiserver.domain.purchase.PurchaseType
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.dto.card.reqeust.CardAddRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseAddRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseFindOfPageRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseModifyRequest
import com.hjj.apiserver.repository.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
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
    fun clean() {
        entityManager.createNativeQuery(
            "SET REFERENTIAL_INTEGRITY FALSE; " +
                    "TRUNCATE TABLE tb_category; " +
                    "TRUNCATE TABLE tb_account_book_user; " +
                    "TRUNCATE TABLE tb_account_book; " +
                    "TRUNCATE TABLE tb_purchase; " +
                    "TRUNCATE TABLE tb_user; " +
                    "TRUNCATE TABLE tb_card; " +
                    "SET REFERENTIAL_INTEGRITY TRUE; "
        ).executeUpdate()
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
        val insertCard = cardService.findCard(
            savedUser.userNo!!, CardAddRequest(
                cardName = "테스트카드",
                cardType = CardType.CHECK_CARD,
                cardDesc = "카드설명",
            )
        )


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
    @DisplayName("소비목록이 페이징처리하여 잘 조회된다.")
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
        val insertCard = cardService.findCard(
            savedUser.userNo!!, CardAddRequest(
                cardName = "테스트카드",
                cardType = CardType.CHECK_CARD,
                cardDesc = "카드설명",
            )
        )

        for (i in 0..30) {
            purchaseService.addPurchase(
                savedUser.userNo!!, PurchaseAddRequest(
                    accountBookNo = savedAccountBook.accountBookNo!!,
                    purchaseType = PurchaseType.OUTGOING,
                    cardNo = insertCard.cardNo,
                    price = 100 + i,
                    reason = "껌값${i}",
                    purchaseDate = LocalDate.now(),
                )
            )
        }


        // when

        val size = 2
        val findPurchasesOfPage = purchaseService.findPurchasesOfPage(
            PurchaseFindOfPageRequest(
                accountBookNo = savedAccountBook.accountBookNo!!,
                startDate = LocalDate.now().minusDays(1),
                endDate = LocalDate.now().plusDays(1),
                size = size,
            ),
            PageRequest.of(
                0,
                size,
            )
        )
        // then
        assertThat(findPurchasesOfPage.size).isEqualTo(size)
        assertThat(findPurchasesOfPage.isFirst).isTrue
        assertThat(findPurchasesOfPage.isLast).isFalse


    }

    @Test
    @DisplayName("Purchase delete 플레그가 정상 Y로 변경된다.")
    fun deletePurchase() {
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
        val insertCard = cardService.findCard(
            savedUser.userNo!!, CardAddRequest(
                cardName = "테스트카드",
                cardType = CardType.CHECK_CARD,
                cardDesc = "카드설명",
            )
        )

        val addPurchase = purchaseService.addPurchase(
            savedUser.userNo!!, PurchaseAddRequest(
                accountBookNo = savedAccountBook.accountBookNo!!,
                purchaseType = PurchaseType.OUTGOING,
                cardNo = insertCard.cardNo,
                price = 100,
                reason = "껌값",
                purchaseDate = LocalDate.now(),
            )
        )

        // when
        purchaseService.removePurchase(savedUser.userNo!!, addPurchase.purchaseNo!!)

        //then
        assertThat(addPurchase.deleteYn).isEqualTo('Y')
    }

    @Test
    @DisplayName("가계 작성내용이 정상 수정된다.")
    fun modifyPurchase() {
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
        val insertCard = cardService.findCard(
            savedUser.userNo!!, CardAddRequest(
                cardName = "테스트카드",
                cardType = CardType.CHECK_CARD,
                cardDesc = "카드설명",
            )
        )

        val addPurchase = purchaseService.addPurchase(
            savedUser.userNo!!, PurchaseAddRequest(
                accountBookNo = savedAccountBook.accountBookNo!!,
                purchaseType = PurchaseType.OUTGOING,
                cardNo = insertCard.cardNo,
                categoryNo = 1,
                price = 100,
                reason = "껌값",
                purchaseDate = LocalDate.now(),
            )
        )


        // when
        purchaseService.modifyPurchase(
            savedUser.userNo!!, addPurchase.purchaseNo!!,
            PurchaseModifyRequest(
                accountBookNo = savedAccountBook.accountBookNo!!,
                cardNo = null,
                purchaseType = PurchaseType.INCOME,
                price = 150,
                reason = "테스트용도",
                purchaseDate = LocalDate.of(2022,1,1)
            )
        )


        // then
        val foundPurchase = purchaseService.findPurchase(savedUser.userNo!!, addPurchase.purchaseNo!!)
        assertThat(foundPurchase.cardNo).isNull()
        assertThat(foundPurchase.categoryNo).isNull()
        assertThat(foundPurchase.purchaseType).isEqualTo(PurchaseType.INCOME)
        assertThat(foundPurchase.price).isEqualTo(150)
        assertThat(foundPurchase.reason).isEqualTo("테스트용도")
        assertThat(foundPurchase.purchaseDate).isEqualTo(LocalDate.of(2022,1,1))

        entityManager.clear()

        // when
        purchaseService.modifyPurchase(
            savedUser.userNo!!, addPurchase.purchaseNo!!,
            PurchaseModifyRequest(
                accountBookNo = savedAccountBook.accountBookNo!!,
                cardNo = 1,
                purchaseType = PurchaseType.OUTGOING,
                categoryNo = 2,
                price = 150,
                reason = "테스트용도",
                purchaseDate = LocalDate.of(2022,1,1)
            )
        )


        // then
        val foundPurchase2 = purchaseService.findPurchase(savedUser.userNo!!, addPurchase.purchaseNo!!)
        assertThat(foundPurchase2.cardNo).isEqualTo(1)
        assertThat(foundPurchase2.categoryNo).isEqualTo(2)
        assertThat(foundPurchase2.price).isEqualTo(150)
        assertThat(foundPurchase2.reason).isEqualTo("테스트용도")
        assertThat(foundPurchase2.purchaseDate).isEqualTo(LocalDate.of(2022,1,1))

    }

    @Test
    @DisplayName("소비 상세가 조회된다.")
    fun findPurchase() {
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
        val insertCard = cardService.findCard(
            savedUser.userNo!!, CardAddRequest(
                cardName = "테스트카드",
                cardType = CardType.CHECK_CARD,
                cardDesc = "카드설명",
            )
        )

        val addPurchase = purchaseService.addPurchase(
            savedUser.userNo!!, PurchaseAddRequest(
                accountBookNo = savedAccountBook.accountBookNo!!,
                purchaseType = PurchaseType.OUTGOING,
                cardNo = insertCard.cardNo,
                categoryNo = 1,
                price = 100,
                reason = "껌값",
                purchaseDate = LocalDate.now(),
            )
        )


        // when
        val foundPurchase = purchaseService.findPurchase(savedUser.userNo!!, addPurchase.purchaseNo!!)

        // then
        assertThat(foundPurchase.purchaseType).isEqualTo(PurchaseType.OUTGOING)
        assertThat(foundPurchase.purchaseDate).isEqualTo(LocalDate.now())
        assertThat(foundPurchase.cardNo).isEqualTo(insertCard.cardNo)
        assertThat(foundPurchase.categoryNo).isEqualTo(1)
        assertThat(foundPurchase.price).isEqualTo(100)
        assertThat(foundPurchase.reason).isEqualTo("껌값")
    }
}