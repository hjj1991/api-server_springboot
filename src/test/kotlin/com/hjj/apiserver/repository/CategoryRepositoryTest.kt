package com.hjj.apiserver.repository

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import org.assertj.core.api.Assertions
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

        accountBookUserRepository.save(
            AccountBookUser(
                accountBook = accountBook, userEntity = tester1, accountRole = AccountRole.OWNER,
                backGroundColor = "#00000", color = "#11111"
            )
        )
        val parentCategory1 = categoryRepository.save(
            Category(
                categoryName = "부모카테고리1",
                categoryDesc = "부모카테고리1 설명",
                categoryIcon = "",
                accountBook = accountBook,
            )
        )
        val parentCategory2 = categoryRepository.save(
            Category(
                categoryName = "부모카테고리2",
                categoryDesc = "부모카테고리2 설명",
                categoryIcon = "",
                accountBook = accountBook,
            )
        )
        val parentCategory3 = categoryRepository.save(
            Category(
                categoryName = "부모카테고리3",
                categoryDesc = "부모카테고리3 설명",
                categoryIcon = "",
                accountBook = accountBook,
            )
        )
        val category1 = categoryRepository.save(
            Category(
                categoryName = "자식카테고리1",
                categoryDesc = "자식카테고리1 설명",
                categoryIcon = "",
                accountBook = accountBook,
                parentCategory = parentCategory1,
            )
        )
        val category2 = categoryRepository.save(
            Category(
                categoryName = "자식카테고리2",
                categoryDesc = "자식카테고리2 설명",
                categoryIcon = "",
                accountBook = accountBook,
                parentCategory = parentCategory1,
            )
        )

        val category3 = categoryRepository.save(
            Category(
                categoryName = "자식카테고리3",
                categoryDesc = "자식카테고리3 설명",
                categoryIcon = "",
                accountBook = accountBook,
                parentCategory = parentCategory3,
            )
        )


        // when
        val findCategories = categoryRepository.findCategories(tester1.userNo!!, accountBook.accountBookNo!!)


        // then
        Assertions.assertThat(findCategories.size).isEqualTo(3)
        Assertions.assertThat(findCategories[0].categoryNo).isEqualTo(parentCategory1.categoryNo)
        Assertions.assertThat(findCategories[1].categoryNo).isEqualTo(parentCategory2.categoryNo)
        Assertions.assertThat(findCategories[2].categoryNo).isEqualTo(parentCategory3.categoryNo)
        Assertions.assertThat(findCategories[0].childCategories).extracting("categoryNo").contains(category1.categoryNo)
        Assertions.assertThat(findCategories[1].childCategories).isEmpty()
        Assertions.assertThat(findCategories[2].childCategories[0].categoryNo).isEqualTo(category3.categoryNo)

    }


    @Test
    @DisplayName("카테고리 권한이 있는 경우 정상 조회된다.")
    fun findCategoryByAccountRoleTest_success() {
        // given
        val tester1 = createUser("tester1", "테스터1")

        val accountBook =
            accountBookRepository.save(AccountBook(accountBookName = "testBookName", accountBookDesc = "testBookDesc"))
        accountBookUserRepository.save(
            AccountBookUser(
                accountBook = accountBook, userEntity = tester1, accountRole = AccountRole.OWNER,
                backGroundColor = "#00000", color = "#11111"
            )
        )
        val parentCategory1 = categoryRepository.save(
            Category(
                categoryName = "부모카테고리1",
                categoryDesc = "부모카테고리1 설명",
                categoryIcon = "",
                accountBook = accountBook,
            )
        )
        categoryRepository.save(
            Category(
                categoryName = "자식카테고리1",
                categoryDesc = "자식카테고리1 설명",
                categoryIcon = "",
                accountBook = accountBook,
                parentCategory = parentCategory1,
            )
        )


        // when
        val findCategory = categoryRepository.findCategoryByAccountRole(
            parentCategory1.categoryNo!!, accountBook.accountBookNo!!,
            tester1.userNo!!
        )


        // then
        Assertions.assertThat(findCategory).isEqualTo(parentCategory1)

    }

    @Test
    @DisplayName("카테고리 권한이 GUEST 경우 조회가 되지 않는다.")
    fun findCategoryByAccountRoleTest_fail_when_accountRole_is_GUEST() {
        // given
        val tester1 = createUser("tester1", "테스터1")

        val accountBook =
            accountBookRepository.save(AccountBook(accountBookName = "testBookName", accountBookDesc = "testBookDesc"))
        accountBookUserRepository.save(
            AccountBookUser(
                accountBook = accountBook, userEntity = tester1, accountRole = AccountRole.GUEST,
                backGroundColor = "#00000", color = "#11111"
            )
        )
        val parentCategory1 = categoryRepository.save(
            Category(
                categoryName = "부모카테고리1",
                categoryDesc = "부모카테고리1 설명",
                categoryIcon = "",
                accountBook = accountBook,
            )
        )
        categoryRepository.save(
            Category(
                categoryName = "자식카테고리1",
                categoryDesc = "자식카테고리1 설명",
                categoryIcon = "",
                accountBook = accountBook,
                parentCategory = parentCategory1,
            )
        )


        // when
        val findCategory = categoryRepository.findCategoryByAccountRole(
            parentCategory1.categoryNo!!, accountBook.accountBookNo!!,
            tester1.userNo!!
        )


        // then
        Assertions.assertThat(findCategory).isNull()

    }

}