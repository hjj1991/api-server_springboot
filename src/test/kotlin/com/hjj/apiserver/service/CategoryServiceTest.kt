package com.hjj.apiserver.service

import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
import com.hjj.apiserver.common.ErrCode
import com.hjj.apiserver.common.exception.AccountBookAccessDeniedException
import com.hjj.apiserver.common.exception.AccountBookNotFoundException
import com.hjj.apiserver.common.exception.CategoryNotFoundException
import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.dto.accountbook.AccountBookDto
import com.hjj.apiserver.dto.category.CategoryDto
import com.hjj.apiserver.dto.category.request.CategoryAddRequest
import com.hjj.apiserver.dto.category.request.CategoryModifyRequest
import com.hjj.apiserver.dto.category.response.CategoryDetailResponse
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.service.impl.CategoryService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.ZonedDateTime

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
    fun addCategory_success() {
        // Given
        val categoryAddRequest =
            CategoryAddRequest(
                accountBookNo = 1L,
                categoryName = "카테고리명",
                categoryDesc = "카테고리 설명",
                categoryIcon = "abc.png",
            )
        val accountBookDto =
            AccountBookDto(
                accountBookNo = 1L,
                accountBookName = "가계부명",
                accountBookDesc = "가계부 설명",
                backgroundColor = "#00000",
                color = "#11111",
                accountRole = AccountRole.OWNER,
                createdAt = ZonedDateTime.now(),
            )

        val accountBook =
            AccountBook(
                accountBookDto.accountBookNo,
                accountBookDto.accountBookName,
                accountBookDto.accountBookDesc,
            )

        val savedCategory =
            Category(
                categoryNo = 1L,
                categoryName = categoryAddRequest.categoryName,
                categoryDesc = categoryAddRequest.categoryDesc,
                categoryIcon = categoryAddRequest.categoryIcon,
                accountBook = accountBook,
                parentCategory = null,
            )

        val savedUser = createUser()

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
    fun addCategory_success_exists_parentCategory() {
        // Given
        val categoryAddRequest =
            CategoryAddRequest(
                accountBookNo = 1L,
                categoryName = "카테고리명",
                categoryDesc = "카테고리 설명",
                categoryIcon = "abc.png",
                parentCategoryNo = 2L,
            )
        val accountBookDto =
            AccountBookDto(
                accountBookNo = 1L,
                accountBookName = "가계부명",
                accountBookDesc = "가계부 설명",
                backgroundColor = "#00000",
                color = "#11111",
                accountRole = AccountRole.OWNER,
                createdAt = ZonedDateTime.now(),
            )

        val accountBook =
            AccountBook(
                accountBookDto.accountBookNo,
                accountBookDto.accountBookName,
                accountBookDto.accountBookDesc,
            )

        val parentCategory =
            Category(
                categoryNo = 2L,
                categoryName = "부모카테고리",
                categoryDesc = "부모카테고리 설명",
                categoryIcon = "parentCategory.png",
                accountBook = accountBook,
                parentCategory = null,
            )

        val savedCategory =
            Category(
                categoryNo = 1L,
                categoryName = categoryAddRequest.categoryName,
                categoryDesc = categoryAddRequest.categoryDesc,
                categoryIcon = categoryAddRequest.categoryIcon,
                accountBook = accountBook,
                parentCategory = parentCategory,
            )

        val savedUser = createUser()

        Mockito.`when`(accountBookRepository.getReferenceById(accountBookDto.accountBookNo))
            .thenReturn(accountBook)

        Mockito.`when`(accountBookRepository.findAccountBook(savedUser.userNo!!, categoryAddRequest.accountBookNo))
            .thenReturn(accountBookDto)

        Mockito.`when`(
            categoryRepository.findCategoryByCategoryNoAndAccountBookAccountBookNoAndIsDeleteIsFalse(
                categoryAddRequest.parentCategoryNo!!,
                categoryAddRequest.accountBookNo,
            ),
        )
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

    @DisplayName("카테고리가 생성시 가계부가 없는 경우 AccountBookNotFoundException 발생한다.")
    @Test
    fun addCategory_fail_when_accountBookNotFound_throw_AccountBookNotFoundException() {
        // Given
        val categoryAddRequest =
            CategoryAddRequest(
                accountBookNo = 999L,
                categoryName = "카테고리명",
                categoryDesc = "카테고리 설명",
                categoryIcon = "abc.png",
            )

        val savedUser = createUser()

        // When & Then
        Assertions.assertThatThrownBy { categoryService.addCategory(savedUser.userNo!!, categoryAddRequest) }
            .isInstanceOf(AccountBookNotFoundException::class.java)
    }

    @DisplayName("모든 카테고리가 정상 조회된다.")
    @Test
    fun findAllCategories_success() {
        // Given
        val accountBook =
            AccountBook(
                13L,
                "가계부이름",
                "가계부 설명",
            )

        val category1 =
            CategoryDto(
                categoryNo = 1L,
                categoryName = "카테고리1",
                categoryDesc = "카테고리설명1",
                categoryIcon = "",
                accountBookNo = accountBook.accountBookNo!!,
            )
        val category2 =
            CategoryDto(
                categoryNo = 2L,
                categoryName = "카테고리2",
                categoryDesc = "카테고리설명2",
                categoryIcon = "",
                accountBookNo = accountBook.accountBookNo!!,
                childCategories =
                    listOf(
                        CategoryDto.ChildCategory(
                            categoryNo = 3L,
                            categoryName = "자식카테고리",
                            categoryDesc = "자식카테고리 설명",
                            categoryIcon = "",
                            accountBookNo = accountBook.accountBookNo!!,
                            parentCategoryNo = 2L,
                        ),
                    ).toMutableList(),
            )

        val categories = listOf(category1, category2)

        val savedUser = createUser()

        Mockito.`when`(accountBookUserRepository.findAccountRole(savedUser.userNo!!, accountBook.accountBookNo!!))
            .thenReturn(AccountRole.OWNER)

        Mockito.`when`(categoryRepository.findCategories(savedUser.userNo!!, accountBook.accountBookNo!!))
            .thenReturn(categories)

        // When
        val categoriesResponse = categoryService.findAllCategories(savedUser.userNo!!, accountBook.accountBookNo!!)

        // Then
        Assertions.assertThat(categoriesResponse.categories.size).isEqualTo(2)
        Assertions.assertThat(categoriesResponse.accountRole).isEqualTo(AccountRole.OWNER)
        Assertions.assertThat(categoriesResponse.categories[0].categoryNo).isEqualTo(category1.categoryNo)
        Assertions.assertThat(categoriesResponse.categories[1].categoryNo).isEqualTo(category2.categoryNo)
    }

    @DisplayName("모든 카테고리 조회시 권한이 없는 경우 AccountBookAccessDeniedException가 발생한다.")
    @Test
    fun findAllCategories_fail_when_notHasReadPermission() {
        // Given
        val accountBook =
            AccountBook(
                13L,
                "가계부이름",
                "가계부 설명",
            )

        val savedUser = createUser()

        Mockito.`when`(accountBookUserRepository.findAccountRole(savedUser.userNo!!, accountBook.accountBookNo!!))
            .thenReturn(AccountRole.GUEST)

        // When && Then
        Assertions.assertThatThrownBy {
            categoryService.findAllCategories(
                savedUser.userNo!!,
                accountBook.accountBookNo!!,
            )
        }
            .isInstanceOf(AccountBookAccessDeniedException::class.java)
    }

    @DisplayName("카테고리 조회가 정상적으로 성공한다.")
    @Test
    fun findCategory_success() {
        // Given
        val accountBook =
            AccountBook(
                13L,
                "가계부이름",
                "가계부 설명",
            )

        val parentCategory =
            Category(
                categoryNo = 3L,
                categoryName = "자식카테고리",
                categoryDesc = "자식카테고리 설명",
                categoryIcon = "",
                accountBook = accountBook,
            )

        val category =
            Category(
                categoryNo = 2L,
                categoryName = "자식카테고리",
                categoryDesc = "자식카테고리 설명",
                categoryIcon = "",
                accountBook = accountBook,
                parentCategory = parentCategory,
            )

        val categoryResponse =
            CategoryDetailResponse(
                accountBookNo = accountBook.accountBookNo!!,
                categoryNo = category.categoryNo!!,
                parentCategoryNo = 3L,
                categoryName = category.categoryName,
                categoryDesc = category.categoryDesc,
                categoryIcon = category.categoryIcon,
            )

        val savedUser = createUser()

        Mockito.`when`(accountBookUserRepository.findAccountRole(savedUser.userNo!!, accountBook.accountBookNo!!))
            .thenReturn(AccountRole.MEMBER)

        Mockito.`when`(categoryRepository.findCategoryByCategoryNo(savedUser.userNo!!, category.categoryNo!!))
            .thenReturn(category)

        // When
        val findCategory = categoryService.findCategory(savedUser.userNo!!, category.categoryNo!!)

        // Then
        Assertions.assertThat(findCategory).isEqualTo(categoryResponse)
    }

    @DisplayName("카테고리 조회시 권한이 없는 경우 AccountBookAccessDeniedException가 발생한다.")
    @Test
    fun findCategory_fail_when_notHasReadPermission() {
        // Given
        val accountBook =
            AccountBook(
                13L,
                "가계부이름",
                "가계부 설명",
            )

        val category =
            Category(
                categoryNo = 2L,
                categoryName = "자식카테고리",
                categoryDesc = "자식카테고리 설명",
                categoryIcon = "",
                accountBook = accountBook,
            )

        val savedUser = createUser()

        Mockito.`when`(categoryRepository.findCategoryByCategoryNo(savedUser.userNo!!, category.categoryNo!!))
            .thenReturn(category)
        Mockito.`when`(accountBookUserRepository.findAccountRole(savedUser.userNo!!, accountBook.accountBookNo!!))
            .thenReturn(AccountRole.GUEST)

        // When && Then
        Assertions.assertThatThrownBy { categoryService.findCategory(savedUser.userNo!!, category.categoryNo!!) }
            .isInstanceOf(AccountBookAccessDeniedException::class.java)
    }

    @DisplayName("카테고리 조회시 카테고리가 없는 경우 CategoryNotFoundException가 발생한다.")
    @Test
    fun findCategory_fail_when_categoryNotFound_throw_CategoryNotException() {
        // Given
        val accountBook =
            AccountBook(
                13L,
                "가계부이름",
                "가계부 설명",
            )

        val category =
            Category(
                categoryNo = 2L,
                categoryName = "자식카테고리",
                categoryDesc = "자식카테고리 설명",
                categoryIcon = "",
                accountBook = accountBook,
            )

        val savedUser = createUser()

        Mockito.`when`(categoryRepository.findCategoryByCategoryNo(savedUser.userNo!!, category.categoryNo!!))
            .thenReturn(null)

        // When && Then
        Assertions.assertThatThrownBy { categoryService.findCategory(savedUser.userNo!!, category.categoryNo!!) }
            .isInstanceOf(CategoryNotFoundException::class.java)
    }

    @DisplayName("카테고리 수정이 정상적으로 성공한다.")
    @Test
    fun modifyCategory_success() {
        // Given
        val accountBook =
            AccountBook(
                13L,
                "가계부이름",
                "가계부 설명",
            )

        val parentCategory =
            Category(
                categoryNo = 3L,
                categoryName = "부모카테고리",
                categoryDesc = "부모카테고리 설명",
                categoryIcon = "",
                accountBook = accountBook,
            )

        val category = createCategory(accountBook, parentCategory)

        val savedUser = createUser()

        val categoryModifyRequest =
            CategoryModifyRequest(
                accountBookNo = accountBook.accountBookNo!!,
                categoryName = "수정명",
                categoryDesc = "수정설명",
                categoryIcon = "",
            )

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                category.categoryNo!!,
                categoryModifyRequest.accountBookNo,
                savedUser.userNo!!,
                setOf(AccountRole.OWNER),
            ),
        )
            .thenReturn(category)

        // When
        val modifyCategory =
            categoryService.modifyCategory(savedUser.userNo!!, category.categoryNo!!, categoryModifyRequest)

        // Then
        Assertions.assertThat(modifyCategory.categoryName).isEqualTo(categoryModifyRequest.categoryName)
        Assertions.assertThat(modifyCategory.categoryIcon).isEqualTo(categoryModifyRequest.categoryIcon)
        Assertions.assertThat(modifyCategory.categoryDesc).isEqualTo(categoryModifyRequest.categoryDesc)
    }

    @DisplayName("카테고리가 없는 경우 CategoryNotFoundException 발생한다. ")
    @Test
    fun modifyCategory_fail_when_category_not_exists_throw_categoryNotFoundException() {
        // Given
        val accountBook =
            AccountBook(
                13L,
                "가계부이름",
                "가계부 설명",
            )

        val parentCategory =
            Category(
                categoryNo = 3L,
                categoryName = "부모카테고리",
                categoryDesc = "부모카테고리 설명",
                categoryIcon = "",
                accountBook = accountBook,
            )

        val category = createCategory(accountBook, parentCategory)

        val savedUser = createUser()

        val categoryModifyRequest =
            CategoryModifyRequest(
                accountBookNo = accountBook.accountBookNo!!,
                categoryName = "수정명",
                categoryDesc = "수정설명",
                categoryIcon = "",
            )

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                category.categoryNo!!,
                categoryModifyRequest.accountBookNo,
                savedUser.userNo!!,
                setOf(AccountRole.OWNER),
            ),
        ).thenReturn(null)

        // When && Then
        Assertions.assertThatThrownBy {
            categoryService.modifyCategory(savedUser.userNo!!, category.categoryNo!!, categoryModifyRequest)
        }
            .isInstanceOf(CategoryNotFoundException::class.java)
    }

    @DisplayName("최상위 카테고리를 자식카테고리로 변경하려할 시 IllegalArgumentException이 발생한다.")
    @Test
    fun modifyCategory_fail_when_parentCategory_not_change_child_category() {
        // Given
        val accountBook =
            AccountBook(
                13L,
                "가계부이름",
                "가계부 설명",
            )

        val parentCategory =
            Category(
                categoryNo = 3L,
                categoryName = "부모카테고리",
                categoryDesc = "부모카테고리 설명",
                categoryIcon = "",
                accountBook = accountBook,
            )

        val category = createCategory(accountBook, null)

        val savedUser = createUser()

        val categoryModifyRequest =
            CategoryModifyRequest(
                accountBookNo = accountBook.accountBookNo!!,
                parentCategoryNo = parentCategory.categoryNo!!,
                categoryName = "수정명",
                categoryDesc = "수정설명",
                categoryIcon = "",
            )

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                category.categoryNo!!,
                categoryModifyRequest.accountBookNo,
                savedUser.userNo!!,
                setOf(AccountRole.OWNER),
            ),
        ).thenReturn(category)

        // When && Then
        Assertions.assertThatThrownBy {
            categoryService.modifyCategory(savedUser.userNo!!, category.categoryNo!!, categoryModifyRequest)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @DisplayName("카테고리 변경시 부모 카테고리가 존재하지 않는 경우 CategoryNotFoundException이 발생한다.")
    @Test
    fun modifyCategory_fail_when_parentCategory_not_found_throw_CategoryNotFoundException() {
        // Given
        val accountBook =
            AccountBook(
                13L,
                "가계부이름",
                "가계부 설명",
            )

        val parentCategory =
            Category(
                categoryNo = 3L,
                categoryName = "부모카테고리",
                categoryDesc = "부모카테고리 설명",
                categoryIcon = "",
                accountBook = accountBook,
            )

        val category = createCategory(accountBook, parentCategory)

        val savedUser = createUser()

        val categoryModifyRequest =
            CategoryModifyRequest(
                accountBookNo = accountBook.accountBookNo!!,
                parentCategoryNo = 10L,
                categoryName = "수정명",
                categoryDesc = "수정설명",
                categoryIcon = "",
            )

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                category.categoryNo!!,
                categoryModifyRequest.accountBookNo,
                savedUser.userNo!!,
                setOf(AccountRole.OWNER),
            ),
        ).thenReturn(category)

        Mockito.`when`(
            categoryRepository.findCategoryByCategoryNoAndAccountBookAccountBookNoAndIsDeleteIsFalse(
                10L,
                accountBook.accountBookNo!!,
            ),
        ).thenReturn(null)

        // When && Then
        Assertions.assertThatThrownBy {
            categoryService.modifyCategory(savedUser.userNo!!, category.categoryNo!!, categoryModifyRequest)
        }
            .isInstanceOf(CategoryNotFoundException::class.java)
            .hasMessage(ErrCode.ERR_CODE0011.msg)
    }

    @DisplayName("카테고리 삭제시 상태값이 정상 변경된다.")
    @Test
    fun deleteCategory_success() {
        // Given
        val accountBook =
            AccountBook(
                13L,
                "가계부이름",
                "가계부 설명",
            )

        val category = Mockito.mock(Category::class.java)

        val savedUser = createUser()

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                category.categoryNo!!,
                accountBook.accountBookNo!!,
                savedUser.userNo!!,
                setOf(AccountRole.OWNER),
            ),
        ).thenReturn(category)

        // When
        categoryService.deleteCategory(category.categoryNo!!, accountBook.accountBookNo!!, savedUser.userNo!!)

        // Then
        Mockito.verify(category, Mockito.times(1)).delete()
    }

    @DisplayName("카테고리 삭제시 카테고리가 없는 경우 CategoryNotFoundException이 발생한다.")
    @Test
    fun deleteCategory_fail_when_categoryNotExists_throw_categoryNotFoundException() {
        // Given

        val savedUser = createUser()

        Mockito.`when`(
            categoryRepository.findCategoryByAccountRole(
                1L,
                1L,
                savedUser.userNo!!,
                setOf(AccountRole.OWNER),
            ),
        ).thenReturn(null)

        // When && Then
        Assertions.assertThatThrownBy { categoryService.deleteCategory(1L, 1L, savedUser.userNo!!) }
            .isInstanceOf(CategoryNotFoundException::class.java)
    }

    private fun createUser(): UserEntity {
        return UserEntity(
            userNo = 1L,
            nickName = "닉네임",
            userEmail = "tester@test.co.kr",
        )
    }

    private fun createCategory(
        accountBook: AccountBook,
        parentCategory: Category?,
    ): Category {
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
