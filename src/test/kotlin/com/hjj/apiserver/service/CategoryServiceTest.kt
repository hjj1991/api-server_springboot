package com.hjj.apiserver.service

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.repository.user.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager

@SpringBootTest
internal class CategoryServiceTest @Autowired constructor(
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val accountBookRepository: AccountBookRepository,
    private val accountBookService: AccountBookService,
    private val entityManager: EntityManager,
) {

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
        categoryService.findAllCategories(savedUser.userNo!!, savedAccountBook.accountBookNo!!)
    }

    @Test
    fun addCategory() {
    }

    @Test
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
        categoryRepository.save(
            Category(
                categoryName = "자식 카테고리1",
                categoryDesc = "설명1",
                categoryIcon = "/test/",
                accountBook = savedAccountBook,
                parentCategory = categoryRepository.getById(1),
            )
        )
        categoryRepository.save(
            Category(
                categoryName = "자식 카테고리2",
                categoryDesc = "설명2",
                categoryIcon = "/test/",
                accountBook = savedAccountBook,
                parentCategory = categoryRepository.getById(1),
            )
        )
        entityManager.clear()

        // when
        val findAllCategories = categoryService.findAllCategories(savedUser.userNo!!, savedAccountBook.accountBookNo!!)


        // then
        println(findAllCategories)
    }

    @Test
    fun findCategory() {
    }

    @Test
    fun modifyCategory() {
    }

    @Test
    fun deleteCategory() {
    }
}