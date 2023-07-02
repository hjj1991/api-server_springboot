package com.hjj.apiserver.service

import com.hjj.apiserver.common.exception.AccountBookNotFoundException
import com.hjj.apiserver.common.exception.CardNotFoundException
import com.hjj.apiserver.common.exception.CategoryNotFoundException
import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.card.CardType
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.domain.purchase.Purchase
import com.hjj.apiserver.domain.purchase.PurchaseType
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.accountbook.AccountBookDto
import com.hjj.apiserver.dto.purchase.request.PurchaseAddRequest
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.card.CardRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.repository.purchase.PurchaseRepository
import com.hjj.apiserver.repository.user.UserRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class PurchaseServiceTest {

    @InjectMocks
    lateinit var purchaseService: PurchaseService

    @Mock
    lateinit var accountBookRepository: AccountBookRepository

    @Mock
    lateinit var categoryRepository: CategoryRepository

    @Mock
    lateinit var accountBookUserRepository: AccountBookUserRepository

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
        val accountBook = AccountBook(
            1L,
            "가계부",
            "가계부 설명"
        )

        val accountBookDto = AccountBookDto(
            accountBook.accountBookNo!!,
            accountBookName = accountBook.accountBookName,
            accountBookDesc = accountBook.accountBookDesc,
            backgroundColor = "#000000",
            color = "#000111",
            accountRole = AccountRole.OWNER,
            createdAt = LocalDateTime.now()
        )

        val category = Category(
            categoryNo = 1L,
            categoryName = "식비",
            categoryDesc = "밥먹을때 사용하는돈",
            categoryIcon = "",
            accountBook = accountBook,
            parentCategory = null
        )

        val savedUser = createUser()
        val card = Card(
            cardNo = 1L,
            cardName = "테스트카드",
            cardType = CardType.CHECK_CARD,
            cardDesc = "카드설명",
            user = savedUser
        )

        val purchaseAddRequest = PurchaseAddRequest(
            accountBookNo = accountBook.accountBookNo!!,
            cardNo = card.cardNo!!,
            categoryNo = category.categoryNo!!,
            purchaseType = PurchaseType.OUTGOING,
            price = 10000,
            reason = "점심비",
            purchaseDate = LocalDate.of(2023, 7, 13)
        )

        val purchase = Purchase(
            purchaseNo = 1L,
            purchaseType = purchaseAddRequest.purchaseType,
            price = purchaseAddRequest.price,
            reason = purchaseAddRequest.reason,
            purchaseDate = purchaseAddRequest.purchaseDate,
            card = card,
            category = category,
            user = savedUser,
            accountBook = accountBook
        )


        Mockito.`when`(accountBookRepository.findAccountBook(savedUser.userNo!!, purchaseAddRequest.accountBookNo))
            .thenReturn(accountBookDto)

        Mockito.`when`(cardRepository.findById(purchaseAddRequest.cardNo!!))
            .thenReturn(Optional.of(card))

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                category.categoryNo!!,
                accountBook.accountBookNo!!,
                savedUser.userNo!!
            )
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
        val accountBook = AccountBook(
            1L,
            "가계부",
            "가계부 설명"
        )

        val accountBookDto = AccountBookDto(
            accountBook.accountBookNo!!,
            accountBookName = accountBook.accountBookName,
            accountBookDesc = accountBook.accountBookDesc,
            backgroundColor = "#000000",
            color = "#000111",
            accountRole = AccountRole.OWNER,
            createdAt = LocalDateTime.now()
        )
        val savedUser = createUser()

        val purchaseAddRequest = PurchaseAddRequest(
            accountBookNo = accountBook.accountBookNo!!,
            purchaseType = PurchaseType.INCOME,
            price = 10000000,
            reason = "월급",
            purchaseDate = LocalDate.of(2023, 7, 13)
        )

        val purchase = Purchase(
            purchaseNo = 1L,
            purchaseType = purchaseAddRequest.purchaseType,
            price = purchaseAddRequest.price,
            reason = purchaseAddRequest.reason,
            purchaseDate = purchaseAddRequest.purchaseDate,
            user = savedUser,
            accountBook = accountBook
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
        val purchaseAddRequest = PurchaseAddRequest(
            accountBookNo = 1L,
            purchaseType = PurchaseType.OUTGOING,
            price = 10000,
            reason = "점심비",
            purchaseDate = LocalDate.of(2023, 7, 13)
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
        val accountBook = AccountBook(
            1L,
            "가계부",
            "가계부 설명"
        )
        val accountBookDto = AccountBookDto(
            accountBook.accountBookNo!!,
            accountBookName = accountBook.accountBookName,
            accountBookDesc = accountBook.accountBookDesc,
            backgroundColor = "#000000",
            color = "#000111",
            accountRole = AccountRole.OWNER,
            createdAt = LocalDateTime.now()
        )
        val savedUser = createUser()
        val purchaseAddRequest = PurchaseAddRequest(
            accountBookNo = accountBook.accountBookNo!!,
            purchaseType = PurchaseType.OUTGOING,
            cardNo = 1L,
            price = 10000,
            reason = "점심비",
            purchaseDate = LocalDate.of(2023, 7, 13)
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
        val accountBook = AccountBook(
            1L,
            "가계부",
            "가계부 설명"
        )
        val accountBookDto = AccountBookDto(
            accountBook.accountBookNo!!,
            accountBookName = accountBook.accountBookName,
            accountBookDesc = accountBook.accountBookDesc,
            backgroundColor = "#000000",
            color = "#000111",
            accountRole = AccountRole.OWNER,
            createdAt = LocalDateTime.now()
        )
        val savedUser = createUser()
        val purchaseAddRequest = PurchaseAddRequest(
            accountBookNo = accountBook.accountBookNo!!,
            purchaseType = PurchaseType.OUTGOING,
            categoryNo = 1L,
            price = 10000,
            reason = "점심비",
            purchaseDate = LocalDate.of(2023, 7, 13)
        )

        Mockito.`when`(accountBookRepository.findAccountBook(savedUser.userNo!!, purchaseAddRequest.accountBookNo))
            .thenReturn(accountBookDto)

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                purchaseAddRequest.categoryNo!!,
                accountBook.accountBookNo!!,
                savedUser.userNo!!
            )
        )
            .thenReturn(null)


        // When && Then
        Assertions.assertThatThrownBy { purchaseService.addPurchase(savedUser.userNo!!, purchaseAddRequest) }
            .isInstanceOf(CategoryNotFoundException::class.java)

    }

    private fun createUser(): User {
        return User(
            userNo = 1L,
            userId = "testUser",
            nickName = "닉네임",
            userEmail = "tester@test.co.kr"
        )
    }

    private fun createCategory(accountBook: AccountBook, parentCategory: Category?): Category {
        return Category(
            categoryNo = 2L,
            categoryName = "자식카테고리",
            categoryDesc = "자식카테고리 설명",
            categoryIcon = "",
            accountBook = accountBook,
            parentCategory = parentCategory,
        )
    }
}