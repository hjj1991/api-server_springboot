package com.hjj.apiserver.service

import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
import com.hjj.apiserver.adapter.out.persistence.user.UserRepository
import com.hjj.apiserver.common.exception.AccountBookNotFoundException
import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.card.CardType
import com.hjj.apiserver.dto.accountbook.AccountBookDto
import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.dto.accountbook.response.AccountBookDetailResponse
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse
import com.hjj.apiserver.dto.category.CategoryDto
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.card.CardRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.service.impl.AccountBookService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.ZonedDateTime

@ExtendWith(MockitoExtension::class)
class AccountBookServiceTest {
    @InjectMocks
    lateinit var accountBookService: AccountBookService

    @Mock
    lateinit var accountBookRepository: AccountBookRepository

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var accountBookUserRepository: AccountBookUserRepository

    @Mock
    lateinit var categoryRepository: CategoryRepository

    @Mock
    lateinit var cardRepository: CardRepository

    @Test
    @DisplayName("가계부가 정상 생성된다.")
    fun addAccountBook_Success() {
        // given
        val request =
            AccountBookAddRequest(
                accountBookName = "가계부명",
                accountBookDesc = "가계부설명",
                backGroundColor = "#ffffff",
                color = "#000000",
            )
        val savedUserEntity =
            UserEntity(
                userNo = 1L,
                nickName = "닉네임",
                userEmail = "tester@test.co.kr",
            )

        val newAccountBook =
            AccountBook(
                accountBookNo = 1L,
                accountBookName = request.accountBookName,
                accountBookDesc = request.accountBookDesc,
            )

        val newAccountBookUser =
            AccountBookUser(
                accountBookUserNo = 1L,
                accountBook = newAccountBook,
                userEntity = savedUserEntity,
                accountRole = AccountRole.OWNER,
                backGroundColor = request.backGroundColor,
                color = request.color,
            )

        Mockito.`when`(accountBookRepository.save(Mockito.any())).thenReturn(newAccountBook)

        Mockito.`when`(userRepository.getReferenceById(1L))
            .thenReturn(savedUserEntity)

        Mockito.`when`(accountBookUserRepository.save(Mockito.any()))
            .thenReturn(newAccountBookUser)

        // when
        val addAccountBookResponse = accountBookService.addAccountBook(1L, request)

        // then
        assertThat(addAccountBookResponse.accountBookNo).isEqualTo(1L)
        assertThat(addAccountBookResponse.accountBookName).isEqualTo(request.accountBookName)
        assertThat(addAccountBookResponse.accountBookDesc).isEqualTo(request.accountBookDesc)
        assertThat(addAccountBookResponse.accountRole).isEqualTo(AccountRole.OWNER)
        assertThat(addAccountBookResponse.color).isEqualTo(request.color)
        assertThat(addAccountBookResponse.backGroundColor).isEqualTo(request.backGroundColor)
    }

    @Test
    @DisplayName("가계부가 정상 조회된다.")
    fun findAccountBookDetail_Success() {
        // given
        val accountBookNo = 1L
        val userNo = 1L
        val userEntity =
            UserEntity(
                userNo = userNo,
                nickName = "닉네임",
            )
        val accountBookDto =
            AccountBookDto(
                accountBookNo = 1L,
                accountBookName = "테스트가계부",
                accountBookDesc = "설명",
                backgroundColor = "#fadvs",
                color = "#fadvs",
                accountRole = AccountRole.OWNER,
                createdAt = ZonedDateTime.now(),
            )

        val cards =
            mutableListOf(
                Card(
                    cardNo = 1L,
                    cardName = "카드",
                    cardType = CardType.CREDIT_CARD,
                    cardDesc = "카드설명",
                    userEntity = userEntity,
                ),
            )

        val categories =
            listOf(
                CategoryDto(
                    categoryNo = 1L,
                    categoryName = "카테고리",
                    categoryDesc = "카테고리 설명",
                    categoryIcon = "아이콘",
                    accountBookNo = accountBookNo,
                    childCategories = mutableListOf(),
                ),
            )

        val accountBookDetailResponse =
            AccountBookDetailResponse(
                accountBookNo = accountBookDto.accountBookNo,
                accountBookName = accountBookDto.accountBookName,
                accountBookDesc = accountBookDto.accountBookDesc,
                accountRole = accountBookDto.accountRole,
                createdAt = accountBookDto.createdAt,
                cards = cards.map(AccountBookDetailResponse.CardDetail::of),
                categories = categories,
            )

        Mockito.`when`(accountBookRepository.findAccountBook(userNo, accountBookNo))
            .thenReturn(accountBookDto)

        Mockito.`when`(cardRepository.findByUserEntityUserNo(userNo))
            .thenReturn(cards)

        Mockito.`when`(categoryRepository.findCategories(userNo, accountBookNo))
            .thenReturn(categories)

        // when
        val findAccountBookDetail = accountBookService.findAccountBookDetail(accountBookNo, userNo)

        // then
        assertThat(findAccountBookDetail.accountBookNo).isEqualTo(accountBookDetailResponse.accountBookNo)
        assertThat(findAccountBookDetail.accountBookName).isEqualTo(accountBookDetailResponse.accountBookName)
        assertThat(findAccountBookDetail.accountBookDesc).isEqualTo(accountBookDetailResponse.accountBookDesc)
        assertThat(findAccountBookDetail.accountRole).isEqualTo(accountBookDetailResponse.accountRole)
        assertThat(findAccountBookDetail.createdAt).isEqualTo(accountBookDetailResponse.createdAt)
        assertThat(findAccountBookDetail.cards[0].cardNo).isEqualTo(accountBookDetailResponse.cards[0].cardNo)
        assertThat(findAccountBookDetail.categories).isEqualTo(accountBookDetailResponse.categories)
    }

    @Test
    @DisplayName("가계부가 없는 경우 AccountBook Not Found Exception 발생.")
    fun findAccountBookDetail_fail_throw_accountBookNotFoundException() {
        // given
        val accountBookNo = 1L
        val userNo = 1L

        Mockito.`when`(accountBookRepository.findAccountBook(userNo, accountBookNo))
            .thenReturn(null)

        // when && then
        assertThatThrownBy { accountBookService.findAccountBookDetail(accountBookNo, userNo) }
            .isInstanceOf(AccountBookNotFoundException::class.java)
    }

    @Test
    @DisplayName("가계부가 전체 조회 된다.")
    fun findAllAccountBook_success() {
        // given
        val userNo = 1L

        val accountBookFindAllResponses =
            listOf(
                AccountBookFindAllResponse(
                    accountBookNo = 1L,
                    accountBookName = "가계부",
                    accountBookDesc = "설명",
                    backGroundColor = "#00000",
                    color = "#00000",
                    accountRole = AccountRole.OWNER,
                ),
            )

        Mockito.`when`(accountBookUserRepository.findAllAccountBookByUserNo(userNo))
            .thenReturn(accountBookFindAllResponses)
        // when
        val findAllAccountBook = accountBookService.findAllAccountBook(userNo)

        // then
        assertThat(findAllAccountBook).isEqualTo(findAllAccountBook)
        assertThat(findAllAccountBook.size).isEqualTo(1)
    }

    // add
    private fun <T> any(): T {
        Mockito.any<T>()
        return null as T
    }
}
