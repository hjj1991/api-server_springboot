package com.hjj.apiserver.service

import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.dto.category.request.CategoryModifyRequest
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.repository.user.UserRepository
import org.assertj.core.api.Assertions.*
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
internal class CategoryServiceTest @Autowired constructor(
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val accountBookRepository: AccountBookRepository,
    private val accountBookUserRepository: AccountBookUserRepository,
    private val accountBookService: AccountBookService,
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
    @DisplayName("기본 카테고리가 정상 추가된다.")
    fun addBasicCategoryTest() {
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


        // when
        val savedAccountBook = accountBookService.addAccountBook(savedUser.userNo!!, accountBookAddRequest)


        // then
        val findAllCategories = categoryService.findAllCategories(savedUser.userNo!!, savedAccountBook.accountBookNo!!)
        assertThat(findAllCategories.categories).hasSize(15)
    }

    @Test
    @DisplayName("카테고리가 정상 추가된다.")
    fun addCategory() {
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
        categoryRepository.save(
            Category(
                categoryName = "자식 카테고리1",
                categoryDesc = "설명1",
                categoryIcon = "/test/",
                accountBook = savedAccountBook,
                parentCategory = categoryRepository.getById(1),
            )
        )
        val savedCategory = categoryRepository.save(
            Category(
                categoryName = "자식 카테고리2",
                categoryDesc = "설명2",
                categoryIcon = "/test/",
                accountBook = savedAccountBook,
                parentCategory = categoryRepository.getById(1),
            )
        )

        // when
        val foundCategory = categoryRepository.findByIdOrNull(savedCategory.categoryNo)?: throw IllegalStateException()
        // then
        assertThat(foundCategory.categoryName).isEqualTo("자식 카테고리2")
        assertThat(foundCategory.categoryDesc).isEqualTo("설명2")
        assertThat(foundCategory.categoryIcon).isEqualTo("/test/")
        assertThat(foundCategory.parentCategory!!.categoryNo).isEqualTo(1)

    }

    @Test
    @DisplayName("모든 카테고리가 정상 조회된다.")
    fun findAllCategoriesTest() {
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

        entityManager.clear()

        // when
        val findAllCategories = categoryService.findAllCategories(savedUser.userNo!!, savedAccountBook.accountBookNo!!)


        // then
        assertThat(findAllCategories.categories).hasSize(15)
    }

    @Test
    @DisplayName("개별 카테고리가 정상 조회된다.")
    fun findCategory() {
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
        categoryRepository.save(
            Category(
                categoryName = "자식 카테고리1",
                categoryDesc = "설명1",
                categoryIcon = "/test/",
                accountBook = savedAccountBook,
                parentCategory = categoryRepository.getById(1),
            )
        )
        val savedCategory = categoryRepository.save(
            Category(
                categoryName = "자식 카테고리2",
                categoryDesc = "설명2",
                categoryIcon = "/test/",
                accountBook = savedAccountBook,
                parentCategory = categoryRepository.getById(1),
            )
        )

        // when
        val foundCategory = categoryRepository.findByIdOrNull(savedCategory.categoryNo)?: throw IllegalStateException()
        // then
        assertThat(foundCategory.categoryName).isEqualTo("자식 카테고리2")
        assertThat(foundCategory.categoryDesc).isEqualTo("설명2")
        assertThat(foundCategory.categoryIcon).isEqualTo("/test/")
        assertThat(foundCategory.parentCategory!!.categoryNo).isEqualTo(1)
    }

    @Test
    @DisplayName("카테고리가 정상 수정된다.")
    fun modifyCategory() {
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
        val savedCategory = categoryRepository.save(
            Category(
                categoryName = "자식 카테고리1",
                categoryDesc = "설명1",
                categoryIcon = "/test/",
                accountBook = savedAccountBook,
                parentCategory = categoryRepository.getById(1),
            )
        )

        categoryService.modifyCategory(savedUser.userNo!!, savedCategory.categoryNo!!, CategoryModifyRequest(
            savedAccountBook.accountBookNo!!,
            parentCategoryNo = 2,
            categoryName = "수정이름",
            categoryDesc = "수정설명",
            categoryIcon = "/수정"
        ))


        // when
        val foundCategory = categoryRepository.findByIdOrNull(savedCategory.categoryNo)?: throw IllegalStateException()

        // then
        assertThat(foundCategory.categoryName).isEqualTo("수정이름")
        assertThat(foundCategory.categoryDesc).isEqualTo("수정설명")
        assertThat(foundCategory.categoryIcon).isEqualTo("/수정")
        assertThat(foundCategory.parentCategory!!.categoryNo).isEqualTo(2)
    }

    @Test
    @DisplayName("카테고리가 정상 삭제된다.")
    fun deleteCategory() {
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
        val savedCategory = categoryRepository.save(
            Category(
                categoryName = "자식 카테고리1",
                categoryDesc = "설명1",
                categoryIcon = "/test/",
                accountBook = savedAccountBook,
                parentCategory = categoryRepository.getById(1),
            )
        )

        categoryService.deleteCategory(savedCategory.categoryNo!!, savedAccountBook.accountBookNo!!, savedUser.userNo!!)
        entityManager.flush()

        // when
        val foundCategory = categoryRepository.findByIdOrNull(savedCategory.categoryNo)

        // then
        assertThat(foundCategory).isNull()
    }
}