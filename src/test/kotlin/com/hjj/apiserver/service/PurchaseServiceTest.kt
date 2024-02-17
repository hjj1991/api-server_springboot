package com.hjj.apiserver.service

import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
import com.hjj.apiserver.adapter.out.persistence.user.UserRepository
import com.hjj.apiserver.common.exception.AccountBookNotFoundException
import com.hjj.apiserver.common.exception.CardNotFoundException
import com.hjj.apiserver.common.exception.CategoryNotFoundException
import com.hjj.apiserver.common.exception.PurchaseNotFoundException
import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.card.CardType
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.domain.purchase.Purchase
import com.hjj.apiserver.domain.purchase.PurchaseType
import com.hjj.apiserver.dto.accountbook.AccountBookDto
import com.hjj.apiserver.dto.purchase.request.PurchaseAddRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseFindOfPageRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseModifyRequest
import com.hjj.apiserver.dto.purchase.response.PurchaseDetailResponse
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.card.CardRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.repository.purchase.PurchaseRepository
import com.hjj.apiserver.service.impl.PurchaseService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class PurchaseServiceTest {
    @InjectMocks
    lateinit var purchaseService: PurchaseService

    @Mock
    lateinit var accountBookRepository: AccountBookRepository

    @Mock
    lateinit var categoryRepository: CategoryRepository

    @Mock
    lateinit var purchaseRepository: PurchaseRepository

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var cardRepository: CardRepository

    @DisplayName("지출이 정상 생성된다.")
    @Test
    fun addPurchase_success_when_outGoing() {
        // Given
        val accountBook =
            AccountBook(
                1L,
                "가계부",
                "가계부 설명",
            )

        val accountBookDto =
            AccountBookDto(
                accountBook.accountBookNo!!,
                accountBookName = accountBook.accountBookName,
                accountBookDesc = accountBook.accountBookDesc,
                backgroundColor = "#000000",
                color = "#000111",
                accountRole = AccountRole.OWNER,
                createdAt = ZonedDateTime.now(),
            )

        val category =
            Category(
                categoryNo = 1L,
                categoryName = "식비",
                categoryDesc = "밥먹을때 사용하는돈",
                categoryIcon = "",
                accountBook = accountBook,
                parentCategory = null,
            )

        val savedUser = createUser()
        val card =
            Card(
                cardNo = 1L,
                cardName = "테스트카드",
                cardType = CardType.CHECK_CARD,
                cardDesc = "카드설명",
                userEntity = savedUser,
            )

        val purchaseAddRequest =
            PurchaseAddRequest(
                accountBookNo = accountBook.accountBookNo!!,
                cardNo = card.cardNo!!,
                categoryNo = category.categoryNo!!,
                purchaseType = PurchaseType.OUTGOING,
                price = 10000,
                reason = "점심비",
                purchaseDate = LocalDate.of(2023, 7, 13),
            )

        val purchase =
            Purchase(
                purchaseNo = 1L,
                purchaseType = purchaseAddRequest.purchaseType,
                price = purchaseAddRequest.price,
                reason = purchaseAddRequest.reason,
                purchaseDate = purchaseAddRequest.purchaseDate,
                card = card,
                category = category,
                userEntity = savedUser,
                accountBook = accountBook,
            )

        Mockito.`when`(accountBookRepository.findAccountBook(savedUser.userNo!!, purchaseAddRequest.accountBookNo))
            .thenReturn(accountBookDto)

        Mockito.`when`(cardRepository.findById(purchaseAddRequest.cardNo!!))
            .thenReturn(Optional.of(card))

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                category.categoryNo!!,
                accountBook.accountBookNo!!,
                savedUser.userNo!!,
            ),
        )
            .thenReturn(category)

        Mockito.`when`(userRepository.getReferenceById(savedUser.userNo!!))
            .thenReturn(savedUser)

        Mockito.`when`(accountBookRepository.getReferenceById(accountBook.accountBookNo!!))
            .thenReturn(accountBook)

        Mockito.`when`(purchaseRepository.save(Mockito.any()))
            .thenReturn(purchase)

        // When
        val purchaseAddResponse = purchaseService.addPurchase(savedUser.userNo!!, purchaseAddRequest)

        // Then
        Assertions.assertThat(purchaseAddResponse.purchaseNo).isEqualTo(purchase.purchaseNo)
        Assertions.assertThat(purchaseAddResponse.purchaseDate).isEqualTo(purchase.purchaseDate)
        Assertions.assertThat(purchaseAddResponse.accountBookNo).isEqualTo(purchase.accountBook.accountBookNo)
        Assertions.assertThat(purchaseAddResponse.categoryNo).isEqualTo(purchase.category!!.categoryNo)
        Assertions.assertThat(purchaseAddResponse.purchaseType).isEqualTo(purchase.purchaseType)
        Assertions.assertThat(purchaseAddResponse.price).isEqualTo(purchase.price)
        Assertions.assertThat(purchaseAddResponse.reason).isEqualTo(purchase.reason)
        Assertions.assertThat(purchaseAddResponse.cardNo).isEqualTo(purchase.card!!.cardNo)
    }

    @DisplayName("수입이 정상 생성된다.")
    @Test
    fun addPurchase_success_when_income() {
        // Given
        val accountBook =
            AccountBook(
                1L,
                "가계부",
                "가계부 설명",
            )

        val accountBookDto =
            AccountBookDto(
                accountBook.accountBookNo!!,
                accountBookName = accountBook.accountBookName,
                accountBookDesc = accountBook.accountBookDesc,
                backgroundColor = "#000000",
                color = "#000111",
                accountRole = AccountRole.OWNER,
                createdAt = ZonedDateTime.now(),
            )
        val savedUser = createUser()

        val purchaseAddRequest =
            PurchaseAddRequest(
                accountBookNo = accountBook.accountBookNo!!,
                purchaseType = PurchaseType.INCOME,
                price = 10000000,
                reason = "월급",
                purchaseDate = LocalDate.of(2023, 7, 13),
            )

        val purchase =
            Purchase(
                purchaseNo = 1L,
                purchaseType = purchaseAddRequest.purchaseType,
                price = purchaseAddRequest.price,
                reason = purchaseAddRequest.reason,
                purchaseDate = purchaseAddRequest.purchaseDate,
                userEntity = savedUser,
                accountBook = accountBook,
            )

        Mockito.`when`(accountBookRepository.findAccountBook(savedUser.userNo!!, purchaseAddRequest.accountBookNo))
            .thenReturn(accountBookDto)

        Mockito.`when`(userRepository.getReferenceById(savedUser.userNo!!))
            .thenReturn(savedUser)

        Mockito.`when`(accountBookRepository.getReferenceById(accountBook.accountBookNo!!))
            .thenReturn(accountBook)

        Mockito.`when`(purchaseRepository.save(Mockito.any()))
            .thenReturn(purchase)

        // When
        val purchaseAddResponse = purchaseService.addPurchase(savedUser.userNo!!, purchaseAddRequest)

        // Then
        Assertions.assertThat(purchaseAddResponse.purchaseNo).isEqualTo(purchase.purchaseNo)
        Assertions.assertThat(purchaseAddResponse.purchaseDate).isEqualTo(purchase.purchaseDate)
        Assertions.assertThat(purchaseAddResponse.accountBookNo).isEqualTo(purchase.accountBook.accountBookNo)
        Assertions.assertThat(purchaseAddResponse.categoryNo).isEqualTo(null)
        Assertions.assertThat(purchaseAddResponse.purchaseType).isEqualTo(purchase.purchaseType)
        Assertions.assertThat(purchaseAddResponse.price).isEqualTo(purchase.price)
        Assertions.assertThat(purchaseAddResponse.reason).isEqualTo(purchase.reason)
        Assertions.assertThat(purchaseAddResponse.cardNo).isEqualTo(null)
    }

    @DisplayName("지출 생성시 가계부가 없는 경우 AccountBookNotFoundException이 발생한다.")
    @Test
    fun addPurchase_fail_when_outGoing_and_accountBookNotExists_throw_AccountBookNotFoundException() {
        // Given
        val savedUser = createUser()
        val purchaseAddRequest =
            PurchaseAddRequest(
                accountBookNo = 1L,
                purchaseType = PurchaseType.OUTGOING,
                price = 10000,
                reason = "점심비",
                purchaseDate = LocalDate.of(2023, 7, 13),
            )

        Mockito.`when`(accountBookRepository.findAccountBook(savedUser.userNo!!, purchaseAddRequest.accountBookNo))
            .thenReturn(null)

        // When && Then
        Assertions.assertThatThrownBy { purchaseService.addPurchase(savedUser.userNo!!, purchaseAddRequest) }
            .isInstanceOf(AccountBookNotFoundException::class.java)
    }

    @DisplayName("지출을 생성시 카드번호를 추가하였으나 해당 카드가 없는 경우 CardNotFoundException이 발생한다.")
    @Test
    fun addPurchase_fail_when_outGoing_and_cardNotExists_throw_CardNotFoundException() {
        // Given
        val accountBook =
            AccountBook(
                1L,
                "가계부",
                "가계부 설명",
            )
        val accountBookDto =
            AccountBookDto(
                accountBook.accountBookNo!!,
                accountBookName = accountBook.accountBookName,
                accountBookDesc = accountBook.accountBookDesc,
                backgroundColor = "#000000",
                color = "#000111",
                accountRole = AccountRole.OWNER,
                createdAt = ZonedDateTime.now(),
            )
        val savedUser = createUser()
        val purchaseAddRequest =
            PurchaseAddRequest(
                accountBookNo = accountBook.accountBookNo!!,
                purchaseType = PurchaseType.OUTGOING,
                cardNo = 1L,
                price = 10000,
                reason = "점심비",
                purchaseDate = LocalDate.of(2023, 7, 13),
            )

        Mockito.`when`(accountBookRepository.findAccountBook(savedUser.userNo!!, purchaseAddRequest.accountBookNo))
            .thenReturn(accountBookDto)

        Mockito.`when`(cardRepository.findById(purchaseAddRequest.cardNo!!))
            .thenReturn(Optional.empty())

        // When && Then
        Assertions.assertThatThrownBy { purchaseService.addPurchase(savedUser.userNo!!, purchaseAddRequest) }
            .isInstanceOf(CardNotFoundException::class.java)
    }

    @DisplayName("지출을 생성시 카테고리번호를 추가하였으나 해당 카테고리가 없는 경우 CategoryNotFoundException이 발생한다.")
    @Test
    fun addPurchase_fail_when_outGoing_and_categoryNotExists_throw_CategoryNotFoundException() {
        // Given
        val accountBook =
            AccountBook(
                1L,
                "가계부",
                "가계부 설명",
            )
        val accountBookDto =
            AccountBookDto(
                accountBook.accountBookNo!!,
                accountBookName = accountBook.accountBookName,
                accountBookDesc = accountBook.accountBookDesc,
                backgroundColor = "#000000",
                color = "#000111",
                accountRole = AccountRole.OWNER,
                createdAt = ZonedDateTime.now(),
            )
        val savedUser = createUser()
        val purchaseAddRequest =
            PurchaseAddRequest(
                accountBookNo = accountBook.accountBookNo!!,
                purchaseType = PurchaseType.OUTGOING,
                categoryNo = 1L,
                price = 10000,
                reason = "점심비",
                purchaseDate = LocalDate.of(2023, 7, 13),
            )

        Mockito.`when`(accountBookRepository.findAccountBook(savedUser.userNo!!, purchaseAddRequest.accountBookNo))
            .thenReturn(accountBookDto)

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                purchaseAddRequest.categoryNo!!,
                accountBook.accountBookNo!!,
                savedUser.userNo!!,
            ),
        )
            .thenReturn(null)

        // When && Then
        Assertions.assertThatThrownBy { purchaseService.addPurchase(savedUser.userNo!!, purchaseAddRequest) }
            .isInstanceOf(CategoryNotFoundException::class.java)
    }

    @DisplayName("수입,지출내역이 정상 조회된다.")
    @Test
    fun findPurchasesOfPage_success() {
        // Given
        val accountBook =
            AccountBook(
                1L,
                "가계부",
                "가계부 설명",
            )
        val savedUser = createUser()

        val card =
            Card(
                cardNo = 1L,
                cardName = "국민",
                cardType = CardType.CHECK_CARD,
                cardDesc = "사치비",
                userEntity = savedUser,
            )

        val category =
            Category(
                categoryNo = 1L,
                categoryName = "식비",
                categoryDesc = "야근하면서 묵을때쓰기",
                categoryIcon = "",
                accountBook = accountBook,
            )

        val purchase1 =
            Purchase(
                purchaseNo = 1L,
                purchaseType = PurchaseType.INCOME,
                price = 1000,
                reason = "월급",
                purchaseDate = LocalDate.of(2023, 7, 3),
                userEntity = savedUser,
                accountBook = accountBook,
            )

        val purchase2 =
            Purchase(
                purchaseNo = 2L,
                purchaseType = PurchaseType.OUTGOING,
                price = 50000,
                reason = "세차비",
                purchaseDate = LocalDate.of(2023, 7, 3),
                userEntity = savedUser,
                accountBook = accountBook,
                card = card,
                category = category,
            )

        val purchase3 =
            Purchase(
                purchaseNo = 3L,
                purchaseType = PurchaseType.OUTGOING,
                price = 3000,
                reason = "밥값",
                purchaseDate = LocalDate.of(2023, 7, 3),
                userEntity = savedUser,
                accountBook = accountBook,
            )

        val startDate = LocalDate.of(2023, 7, 1)
        val endDate = LocalDate.of(2023, 7, 31)

        val pageRequest = PageRequest.of(0, 2)
        val purchaseFindOfPageRequest =
            PurchaseFindOfPageRequest(
                accountBookNo = accountBook.accountBookNo!!,
                startDate = startDate,
                endDate = endDate,
                size = pageRequest.pageSize,
                page = pageRequest.pageNumber,
            )

        Mockito.`when`(
            purchaseRepository.findPurchasePageCustom(
                startDate,
                endDate,
                accountBook.accountBookNo!!,
                pageRequest,
            ),
        )
            .thenReturn(listOf(purchase1, purchase2, purchase3))

        // When
        val findPurchasesOfPage = purchaseService.findPurchasesOfPage(purchaseFindOfPageRequest, pageRequest)

        // Then
        Assertions.assertThat(findPurchasesOfPage.size).isEqualTo(pageRequest.pageSize)
        Assertions.assertThat(findPurchasesOfPage.hasNext()).isTrue()
        Assertions.assertThat(findPurchasesOfPage.content[0].purchaseNo).isEqualTo(purchase1.purchaseNo)
        Assertions.assertThat(findPurchasesOfPage.content[0].purchaseDate).isEqualTo(purchase1.purchaseDate)
        Assertions.assertThat(findPurchasesOfPage.content[0].purchaseType).isEqualTo(purchase1.purchaseType)
        Assertions.assertThat(findPurchasesOfPage.content[0].cardNo).isNull()
        Assertions.assertThat(findPurchasesOfPage.content[0].categoryInfo).isNull()
        Assertions.assertThat(findPurchasesOfPage.content[1].purchaseNo).isEqualTo(purchase2.purchaseNo)
        Assertions.assertThat(findPurchasesOfPage.content[1].purchaseDate).isEqualTo(purchase2.purchaseDate)
        Assertions.assertThat(findPurchasesOfPage.content[1].purchaseType).isEqualTo(purchase2.purchaseType)
        Assertions.assertThat(findPurchasesOfPage.content[1].cardNo).isEqualTo(purchase2.card!!.cardNo)
        Assertions.assertThat(findPurchasesOfPage.content[1].categoryInfo!!.categoryNo)
            .isEqualTo(purchase2.category!!.categoryNo)
    }

    @DisplayName("수입,지출내역이 정상 삭제된다.")
    @Test
    fun removePurchase_success() {
        // Given
        val accountBook =
            AccountBook(
                1L,
                "가계부",
                "가계부 설명",
            )
        val savedUser = createUser()

        val card =
            Card(
                cardNo = 1L,
                cardName = "국민",
                cardType = CardType.CHECK_CARD,
                cardDesc = "사치비",
                userEntity = savedUser,
            )

        val category =
            Category(
                categoryNo = 1L,
                categoryName = "식비",
                categoryDesc = "야근하면서 묵을때쓰기",
                categoryIcon = "",
                accountBook = accountBook,
            )
        val purchase =
            Purchase(
                purchaseNo = 2L,
                purchaseType = PurchaseType.OUTGOING,
                price = 50000,
                reason = "세차비",
                purchaseDate = LocalDate.of(2023, 7, 3),
                userEntity = savedUser,
                accountBook = accountBook,
                card = card,
                category = category,
            )

        Mockito.`when`(
            purchaseRepository.findEntityGraphByUserEntityUserNoAndPurchaseNoAndIsDeleteIsFalse(
                savedUser.userNo!!,
                purchase.purchaseNo!!,
            ),
        ).thenReturn(purchase)

        // When
        purchaseService.removePurchase(savedUser.userNo!!, purchase.purchaseNo!!)

        // Then
        Assertions.assertThat(purchase.isDelete).isTrue()
    }

    @DisplayName("수입,지출내역이 존재하지 않는경우 PurchaseNotFoundException 예외가 발생한다.")
    @Test
    fun removePurchase_fail_when_purchaseNotExists_throw_PurchaseNotFoundException() {
        // Given
        val savedUser = createUser()
        // When && Then
        Assertions.assertThatThrownBy { purchaseService.removePurchase(savedUser.userNo!!, 1L) }
            .isInstanceOf(PurchaseNotFoundException::class.java)
    }

    @DisplayName("수입내역이 수정이 정상적으로 작동한다.")
    @Test
    fun modifyPurchase_success_when_outGoing() {
        // Given
        val accountBook =
            AccountBook(
                1L,
                "가계부",
                "가계부 설명",
            )
        val savedUser = createUser()
        val card =
            Card(
                cardNo = 1L,
                cardName = "국민",
                cardType = CardType.CHECK_CARD,
                cardDesc = "사치비",
                userEntity = savedUser,
            )

        val category =
            Category(
                categoryNo = 1L,
                categoryName = "식비",
                categoryDesc = "야근하면서 묵을때쓰기",
                categoryIcon = "",
                accountBook = accountBook,
            )
        val purchase =
            Purchase(
                purchaseNo = 2L,
                purchaseType = PurchaseType.OUTGOING,
                price = 50000,
                reason = "세차비",
                purchaseDate = LocalDate.of(2023, 7, 3),
                userEntity = savedUser,
                accountBook = accountBook,
            )

        val purchaseModifyRequest =
            PurchaseModifyRequest(
                accountBookNo = accountBook.accountBookNo!!,
                cardNo = card.cardNo,
                categoryNo = category.categoryNo!!,
                purchaseType = PurchaseType.OUTGOING,
                price = 5000000,
                reason = "변경이유",
                purchaseDate = LocalDate.of(2023, 7, 6),
            )

        Mockito.`when`(
            purchaseRepository.findEntityGraphByUserEntityUserNoAndPurchaseNoAndIsDeleteIsFalse(
                savedUser.userNo!!,
                purchase.purchaseNo!!,
            ),
        ).thenReturn(purchase)

        Mockito.`when`(
            cardRepository.findByCardNoAndUserEntityUserNoAndIsDeleteIsFalse(card.cardNo!!, savedUser.userNo!!),
        )
            .thenReturn(card)

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                categoryNo = purchaseModifyRequest.categoryNo!!,
                accountBookNo = accountBook.accountBookNo!!,
                userNo = savedUser.userNo!!,
            ),
        ).thenReturn(category)

        // When
        purchaseService.modifyPurchase(savedUser.userNo!!, purchase.purchaseNo!!, purchaseModifyRequest)

        // Then
        Assertions.assertThat(purchase.category).isEqualTo(category)
        Assertions.assertThat(purchase.card).isEqualTo(card)
        Assertions.assertThat(purchase.price).isEqualTo(5000000)
        Assertions.assertThat(purchase.reason).isEqualTo("변경이유")
        Assertions.assertThat(purchase.purchaseDate).isEqualTo(LocalDate.of(2023, 7, 6))
    }

    @DisplayName("지출내역을 수입 내역으로 수정할 경우 category와 card정보가 null로 초기화된다.")
    @Test
    fun modifyPurchase_success_when_income_then_category_and_card_null_save() {
        // Given
        val accountBook =
            AccountBook(
                1L,
                "가계부",
                "가계부 설명",
            )
        val savedUser = createUser()
        val card =
            Card(
                cardNo = 1L,
                cardName = "국민",
                cardType = CardType.CHECK_CARD,
                cardDesc = "사치비",
                userEntity = savedUser,
            )

        val category =
            Category(
                categoryNo = 1L,
                categoryName = "식비",
                categoryDesc = "야근하면서 묵을때쓰기",
                categoryIcon = "",
                accountBook = accountBook,
            )
        val purchase =
            Purchase(
                purchaseNo = 2L,
                purchaseType = PurchaseType.OUTGOING,
                price = 50000,
                reason = "세차비",
                purchaseDate = LocalDate.of(2023, 7, 3),
                userEntity = savedUser,
                accountBook = accountBook,
            )

        val purchaseModifyRequest =
            PurchaseModifyRequest(
                accountBookNo = accountBook.accountBookNo!!,
                cardNo = card.cardNo,
                categoryNo = category.categoryNo!!,
                purchaseType = PurchaseType.INCOME,
                price = 5000000,
                reason = "변경이유",
                purchaseDate = LocalDate.of(2023, 7, 6),
            )

        Mockito.`when`(
            purchaseRepository.findEntityGraphByUserEntityUserNoAndPurchaseNoAndIsDeleteIsFalse(
                savedUser.userNo!!,
                purchase.purchaseNo!!,
            ),
        ).thenReturn(purchase)

        Mockito.`when`(
            cardRepository.findByCardNoAndUserEntityUserNoAndIsDeleteIsFalse(card.cardNo!!, savedUser.userNo!!),
        )
            .thenReturn(card)

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                categoryNo = purchaseModifyRequest.categoryNo!!,
                accountBookNo = accountBook.accountBookNo!!,
                userNo = savedUser.userNo!!,
            ),
        ).thenReturn(category)

        // When
        purchaseService.modifyPurchase(savedUser.userNo!!, purchase.purchaseNo!!, purchaseModifyRequest)

        // Then
        Assertions.assertThat(purchase.category).isNull()
        Assertions.assertThat(purchase.card).isNull()
        Assertions.assertThat(purchase.price).isEqualTo(5000000)
        Assertions.assertThat(purchase.reason).isEqualTo("변경이유")
        Assertions.assertThat(purchase.purchaseDate).isEqualTo(LocalDate.of(2023, 7, 6))
    }

    @DisplayName("수입내역 수정시 cardNo값이 있는데 DB에 카드가 없는 경우 CardNotFoundException이 발생한다.")
    @Test
    fun modifyPurchase_fail_when_cardNotExists_throw_cardNotFoundException() {
        // Given
        val accountBook =
            AccountBook(
                1L,
                "가계부",
                "가계부 설명",
            )
        val savedUser = createUser()
        val card =
            Card(
                cardNo = 1L,
                cardName = "국민",
                cardType = CardType.CHECK_CARD,
                cardDesc = "사치비",
                userEntity = savedUser,
            )

        val purchase =
            Purchase(
                purchaseNo = 2L,
                purchaseType = PurchaseType.OUTGOING,
                price = 50000,
                reason = "세차비",
                purchaseDate = LocalDate.of(2023, 7, 3),
                userEntity = savedUser,
                accountBook = accountBook,
            )

        val purchaseModifyRequest =
            PurchaseModifyRequest(
                accountBookNo = accountBook.accountBookNo!!,
                cardNo = card.cardNo,
                purchaseType = PurchaseType.OUTGOING,
                price = 5000000,
                reason = "변경이유",
                purchaseDate = LocalDate.of(2023, 7, 6),
            )

        Mockito.`when`(
            purchaseRepository.findEntityGraphByUserEntityUserNoAndPurchaseNoAndIsDeleteIsFalse(
                savedUser.userNo!!,
                purchase.purchaseNo!!,
            ),
        ).thenReturn(purchase)

        Mockito.`when`(
            cardRepository.findByCardNoAndUserEntityUserNoAndIsDeleteIsFalse(card.cardNo!!, savedUser.userNo!!),
        )
            .thenReturn(null)

        // When && Then
        Assertions.assertThatThrownBy {
            purchaseService.modifyPurchase(
                savedUser.userNo!!,
                purchase.purchaseNo!!,
                purchaseModifyRequest,
            )
        }
            .isInstanceOf(CardNotFoundException::class.java)
    }

    @DisplayName("수입내역 수정시 categoryNo값이 있는데 DB에 카테고리가 없는 경우 CategoryNotFoundException이 발생한다.")
    @Test
    fun modifyPurchase_fail_when_categoryNotExists_throw_CategoryNotFoundException() {
        // Given
        val accountBook =
            AccountBook(
                1L,
                "가계부",
                "가계부 설명",
            )
        val savedUser = createUser()
        val card =
            Card(
                cardNo = 1L,
                cardName = "국민",
                cardType = CardType.CHECK_CARD,
                cardDesc = "사치비",
                userEntity = savedUser,
            )

        val purchase =
            Purchase(
                purchaseNo = 2L,
                purchaseType = PurchaseType.OUTGOING,
                price = 50000,
                reason = "세차비",
                purchaseDate = LocalDate.of(2023, 7, 3),
                userEntity = savedUser,
                accountBook = accountBook,
            )

        val purchaseModifyRequest =
            PurchaseModifyRequest(
                accountBookNo = accountBook.accountBookNo!!,
                cardNo = card.cardNo,
                purchaseType = PurchaseType.OUTGOING,
                categoryNo = 5L,
                price = 5000000,
                reason = "변경이유",
                purchaseDate = LocalDate.of(2023, 7, 6),
            )

        Mockito.`when`(
            purchaseRepository.findEntityGraphByUserEntityUserNoAndPurchaseNoAndIsDeleteIsFalse(
                savedUser.userNo!!,
                purchase.purchaseNo!!,
            ),
        ).thenReturn(purchase)

        Mockito.`when`(
            cardRepository.findByCardNoAndUserEntityUserNoAndIsDeleteIsFalse(card.cardNo!!, savedUser.userNo!!),
        )
            .thenReturn(card)

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                categoryNo = purchaseModifyRequest.categoryNo!!,
                accountBookNo = accountBook.accountBookNo!!,
                userNo = savedUser.userNo!!,
            ),
        ).thenReturn(null)

        // When && Then
        Assertions.assertThatThrownBy {
            purchaseService.modifyPurchase(
                savedUser.userNo!!,
                purchase.purchaseNo!!,
                purchaseModifyRequest,
            )
        }
            .isInstanceOf(CategoryNotFoundException::class.java)
    }

    @DisplayName("수입내역 수정시 수입내역이 없는 경우 PurchaseNotFoundException이 발생한다.")
    @Test
    fun modifyPurchase_fail_when_purchaseNotExists_throw_PurchaseNotFoundException() {
        // Given
        val accountBook =
            AccountBook(
                1L,
                "가계부",
                "가계부 설명",
            )
        val savedUser = createUser()
        val card =
            Card(
                cardNo = 1L,
                cardName = "국민",
                cardType = CardType.CHECK_CARD,
                cardDesc = "사치비",
                userEntity = savedUser,
            )

        val purchase =
            Purchase(
                purchaseNo = 2L,
                purchaseType = PurchaseType.OUTGOING,
                price = 50000,
                reason = "세차비",
                purchaseDate = LocalDate.of(2023, 7, 3),
                userEntity = savedUser,
                accountBook = accountBook,
            )

        val purchaseModifyRequest =
            PurchaseModifyRequest(
                accountBookNo = accountBook.accountBookNo!!,
                cardNo = card.cardNo,
                purchaseType = PurchaseType.OUTGOING,
                categoryNo = 5L,
                price = 5000000,
                reason = "변경이유",
                purchaseDate = LocalDate.of(2023, 7, 6),
            )

        Mockito.`when`(
            purchaseRepository.findEntityGraphByUserEntityUserNoAndPurchaseNoAndIsDeleteIsFalse(
                savedUser.userNo!!,
                purchase.purchaseNo!!,
            ),
        ).thenReturn(null)

        // When && Then
        Assertions.assertThatThrownBy {
            purchaseService.modifyPurchase(
                savedUser.userNo!!,
                purchase.purchaseNo!!,
                purchaseModifyRequest,
            )
        }
            .isInstanceOf(PurchaseNotFoundException::class.java)
    }

    @DisplayName("수입,지출내역 상세가 정상 조회된다.")
    @Test
    fun findPurchase_success() {
        // Given
        val accountBook =
            AccountBook(
                1L,
                "가계부",
                "가계부 설명",
            )
        val savedUser = createUser()

        val card =
            Card(
                cardNo = 1L,
                cardName = "국민",
                cardType = CardType.CHECK_CARD,
                cardDesc = "사치비",
                userEntity = savedUser,
            )

        val category =
            Category(
                categoryNo = 1L,
                categoryName = "식비",
                categoryDesc = "야근하면서 묵을때쓰기",
                categoryIcon = "",
                accountBook = accountBook,
            )

        val purchaseDetailResponse =
            PurchaseDetailResponse(
                purchaseNo = 1L,
                accountBookNo = accountBook.accountBookNo!!,
                cardNo = card.cardNo!!,
                categoryNo = category.categoryNo!!,
                purchaseType = PurchaseType.OUTGOING,
                price = 10000,
                reason = "세차비",
                purchaseDate = LocalDate.of(2023, 5, 1),
            )

        Mockito.`when`(purchaseRepository.findPurchase(savedUser.userNo!!, 1L))
            .thenReturn(purchaseDetailResponse)

        // When
        val findPurchase = purchaseService.findPurchase(savedUser.userNo!!, 1L)

        // Then
        Assertions.assertThat(findPurchase).isEqualTo(purchaseDetailResponse)
    }

    @DisplayName("수입,지출내역 상세가 존재하지 않는 경우 PurchaseNotFoundException이 발생한다.")
    @Test
    fun findPurchase_fail_when_purchaseNotExists_PurchaseNotFoundException() {
        // Given
        Mockito.`when`(purchaseRepository.findPurchase(1L, 1L))
            .thenReturn(null)

        // When && Then
        Assertions.assertThatThrownBy { purchaseService.findPurchase(1L, 1L) }
            .isInstanceOf(PurchaseNotFoundException::class.java)
    }

    private fun createUser(): UserEntity {
        return UserEntity(
            userNo = 1L,
            nickName = "닉네임",
            userEmail = "tester@test.co.kr",
        )
    }
}
