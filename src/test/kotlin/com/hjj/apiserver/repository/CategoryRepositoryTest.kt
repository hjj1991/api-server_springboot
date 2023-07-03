package com.hjj.apiserver.repository

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import org.assertj.core.api.Assertions
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CategoryRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var accountBookUserRepository: AccountBookUserRepository

    @Autowired
    private lateinit var accountBookRepository: AccountBookRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Test
    @DisplayName("카테고리 목록이 정상 조회된다.")
    fun findCategoriesTest_success() {
        // given
        val tester1 = createUser("tester1", "테스터1")

        val accountBook =
            accountBookRepository.save(AccountBook(accountBookName = "testBookName", accountBookDesc = "testBookDesc"))


        val accountBookUser = accountBookUserRepository.save(
            AccountBookUser(
                accountBook = accountBook, user = tester1, accountRole = AccountRole.OWNER,
                backGroundColor = "#00000", color = "#11111"
            )
        )
        val parentCategory1 = categoryRepository.save(Category(
            categoryName = "부모카테고리1",
            categoryDesc = "부모카테고리1 설명",
            categoryIcon = "",
            accountBook = accountBook,
        ))
        val parentCategory2 = categoryRepository.save(Category(
            categoryName = "부모카테고리2",
            categoryDesc = "부모카테고리2 설명",
            categoryIcon = "",
            accountBook = accountBook,
        ))
        val parentCategory3 = categoryRepository.save(Category(
            categoryName = "부모카테고리3",
            categoryDesc = "부모카테고리3 설명",
            categoryIcon = "",
            accountBook = accountBook,
        ))
        val category1 = categoryRepository.save(Category(
            categoryName = "자식카테고리1",
            categoryDesc = "자식카테고리1 설명",
            categoryIcon = "",
            accountBook = accountBook,
            parentCategory = parentCategory1,
        ))
        val category2 = categoryRepository.save(Category(
            categoryName = "자식카테고리2",
            categoryDesc = "자식카테고리2 설명",
            categoryIcon = "",
            accountBook = accountBook,
            parentCategory = parentCategory1,
        ))

        val category3 = categoryRepository.save(Category(
            categoryName = "자식카테고리3",
            categoryDesc = "자식카테고리3 설명",
            categoryIcon = "",
            accountBook = accountBook,
            parentCategory = parentCategory3,
        ))


        // when
        val findCategories = categoryRepository.findCategories(tester1.userNo!!, accountBook.accountBookNo!!)


        // then
        Assertions.assertThat(findCategories.size).isEqualTo(3)

    }


    @Test
    @DisplayName("가계부의 해당사용자의 권한 조회가 정상 성공한다.")
    fun findAccountRole_success() {
        // given
        val tester1 = createUser("tester1", "테스터1")
        val accountBook1 =
            accountBookRepository.save(AccountBook(accountBookName = "testBookName", accountBookDesc = "testBookDesc"))
        val accountBookUser1 = accountBookUserRepository.save(
            AccountBookUser(
                accountBook = accountBook1, user = tester1, accountRole = AccountRole.OWNER,
                backGroundColor = "#00000", color = "#11111"
            )
        )

        // when
        val findAccountRole = accountBookUserRepository.findAccountRole(tester1.userNo!!, accountBook1.accountBookNo!!)
        Assertions.assertThat(findAccountRole).isEqualTo(accountBookUser1.accountRole)
    }

}