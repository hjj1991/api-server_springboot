package com.hjj.apiserver.service

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.accountbook.AccountBookDto
import com.hjj.apiserver.dto.category.request.CategoryAddRequest
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class CategoryServiceTest {

    @InjectMocks
    lateinit var categoryService: CategoryService

    @Mock
    lateinit var accountBookRepository: AccountBookRepository

    @Mock
    lateinit var categoryRepository: CategoryRepository

    @Mock
    lateinit var accountBookUserRepository: AccountBookUserRepository

    @DisplayName("카테고리가 정상 생성된다.")
    @Test
    fun addCategory_success(){
        // Given
        val categoryAddRequest = CategoryAddRequest(
            accountBookNo = 1L,
            categoryName = "카테고리명",
            categoryDesc = "카테고리 설명",
            categoryIcon = "abc.png"
        )
        val savedUser = User(
            userNo = 1L,
            userId = "testUser",
            nickName = "닉네임",
            userEmail = "tester@test.co.kr"
        )
        val accountBookDto = AccountBookDto(
            accountBookNo = 1L,
            accountBookName = "가계부명",
            accountBookDesc = "가계부 설명",
            backgroundColor = "#00000",
            color = "#11111",
            accountRole = AccountRole.OWNER,
            createdAt = LocalDateTime.now()
        )

        val accountBook = AccountBook(
            accountBookDto.accountBookNo,
            accountBookDto.accountBookName, accountBookDto.accountBookDesc
        )

        val savedCategory = Category(
            categoryNo = 1L,
            categoryName = categoryAddRequest.categoryName,
            categoryDesc = categoryAddRequest.categoryDesc,
            categoryIcon = categoryAddRequest.categoryIcon,
            accountBook = accountBook,
            parentCategory = null
        )


        Mockito.`when`(accountBookRepository.getReferenceById(accountBookDto.accountBookNo))
            .thenReturn(accountBook)

        Mockito.`when`(accountBookRepository.findAccountBook(savedUser.userNo!!, categoryAddRequest.accountBookNo))
            .thenReturn(accountBookDto)

        Mockito.`when`(categoryRepository.save(Mockito.any()))
            .thenReturn(savedCategory)

        // When
        val categoryAddResponse = categoryService.addCategory(savedUser.userNo!!, categoryAddRequest)


        // Then
        Assertions.assertThat(categoryAddResponse.categoryNo).isEqualTo(savedCategory.categoryNo)
        Assertions.assertThat(categoryAddResponse.accountBookNo).isEqualTo(categoryAddRequest.accountBookNo)
        Assertions.assertThat(categoryAddResponse.categoryName).isEqualTo(categoryAddRequest.categoryName)
        Assertions.assertThat(categoryAddResponse.categoryDesc).isEqualTo(categoryAddRequest.categoryDesc)
        Assertions.assertThat(categoryAddResponse.categoryIcon).isEqualTo(categoryAddRequest.categoryIcon)
        Assertions.assertThat(categoryAddResponse.parentCategoryNo).isEqualTo(categoryAddRequest.parentCategoryNo)

    }

    @DisplayName("부모 카테고리가 존재하는 카테고리가 정상 생성된다.")
    @Test
    fun addCategory_success_exists_parentCategory(){
        // Given
        val categoryAddRequest = CategoryAddRequest(
            accountBookNo = 1L,
            categoryName = "카테고리명",
            categoryDesc = "카테고리 설명",
            categoryIcon = "abc.png",
            parentCategoryNo = 2L
        )
        val savedUser = User(
            userNo = 1L,
            userId = "testUser",
            nickName = "닉네임",
            userEmail = "tester@test.co.kr"
        )
        val accountBookDto = AccountBookDto(
            accountBookNo = 1L,
            accountBookName = "가계부명",
            accountBookDesc = "가계부 설명",
            backgroundColor = "#00000",
            color = "#11111",
            accountRole = AccountRole.OWNER,
            createdAt = LocalDateTime.now()
        )

        val accountBook = AccountBook(
            accountBookDto.accountBookNo,
            accountBookDto.accountBookName, accountBookDto.accountBookDesc
        )

        val parentCategory = Category(
            categoryNo = 2L,
            categoryName = "부모카테고리",
            categoryDesc = "부모카테고리 설명",
            categoryIcon = "parentCategory.png",
            accountBook = accountBook,
            parentCategory = null
        )

        val savedCategory = Category(
            categoryNo = 1L,
            categoryName = categoryAddRequest.categoryName,
            categoryDesc = categoryAddRequest.categoryDesc,
            categoryIcon = categoryAddRequest.categoryIcon,
            accountBook = accountBook,
            parentCategory = parentCategory
        )


        Mockito.`when`(accountBookRepository.getReferenceById(accountBookDto.accountBookNo))
            .thenReturn(accountBook)

        Mockito.`when`(accountBookRepository.findAccountBook(savedUser.userNo!!, categoryAddRequest.accountBookNo))
            .thenReturn(accountBookDto)

        Mockito.`when`(categoryRepository.findCategoryByCategoryNoAndAccountBook_AccountBookNoAndDeleteIsFalse(categoryAddRequest.parentCategoryNo!!, categoryAddRequest.accountBookNo))
            .thenReturn(parentCategory)

        Mockito.`when`(categoryRepository.save(Mockito.any()))
            .thenReturn(savedCategory)

        // When
        val categoryAddResponse = categoryService.addCategory(savedUser.userNo!!, categoryAddRequest)


        // Then
        Assertions.assertThat(categoryAddResponse.categoryNo).isEqualTo(savedCategory.categoryNo)
        Assertions.assertThat(categoryAddResponse.accountBookNo).isEqualTo(categoryAddRequest.accountBookNo)
        Assertions.assertThat(categoryAddResponse.categoryName).isEqualTo(categoryAddRequest.categoryName)
        Assertions.assertThat(categoryAddResponse.categoryDesc).isEqualTo(categoryAddRequest.categoryDesc)
        Assertions.assertThat(categoryAddResponse.categoryIcon).isEqualTo(categoryAddRequest.categoryIcon)
        Assertions.assertThat(categoryAddResponse.parentCategoryNo).isEqualTo(categoryAddRequest.parentCategoryNo)

    }
}