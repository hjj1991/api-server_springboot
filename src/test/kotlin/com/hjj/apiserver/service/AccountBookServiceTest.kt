package com.hjj.apiserver.service

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.card.CardRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.repository.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension


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
        val request = AccountBookAddRequest(
            accountBookName = "가계부명",
            accountBookDesc = "가계부설명",
            backGroundColor = "#ffffff",
            color = "#000000",
        )
        val savedUser = User(
            userNo = 1L,
            userId = "testUser",
            nickName = "닉네임",
            userEmail = "tester@test.co.kr"
        )

        val newAccountBook = AccountBook(
            accountBookNo = 1L,
            accountBookName = request.accountBookName,
            accountBookDesc = request.accountBookDesc
        )

        val newAccountBookUser = AccountBookUser(
            accountBookUserNo = 1L,
            accountBook = newAccountBook,
            user = savedUser,
            accountRole = AccountRole.OWNER,
            backGroundColor = request.backGroundColor,
            color = request.color
        )

        Mockito.`when`(accountBookRepository.save(Mockito.any())).thenReturn(newAccountBook)

        Mockito.`when`(userRepository.getReferenceById(1L))
            .thenReturn(savedUser)

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


}