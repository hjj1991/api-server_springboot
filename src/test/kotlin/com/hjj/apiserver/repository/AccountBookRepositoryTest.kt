package com.hjj.apiserver.repository

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.util.CommonUtils
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.format.DateTimeFormatter


class AccountBookRepositoryTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var accountBookRepository: AccountBookRepository

    @Autowired
    private lateinit var accountBookUserRepository: AccountBookUserRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Test
    @DisplayName("사용자의 가계부가 정상 조회된다.")
    fun findAccountBookTest_success() {
        // given
        val user = createUser()

        val accountBook =
            accountBookRepository.save(AccountBook(accountBookName = "testBookName", accountBookDesc = "testBookDesc"))

        val accountBookUser = accountBookUserRepository.save(
            AccountBookUser(
                accountBook = accountBook, user = user, accountRole = AccountRole.OWNER,
                backGroundColor = "#00000", color = "#11111"
            )
        )

        val createBasicCategories = CommonUtils.createBasicCategories(accountBook)
        categoryRepository.saveAll(createBasicCategories)

        // when
        val findAccountBook = accountBookRepository.findAccountBook(user.userNo!!, accountBook.accountBookNo!!)

        // then
        Assertions.assertThat(findAccountBook!!.accountBookNo).isEqualTo(accountBook.accountBookNo)
        Assertions.assertThat(findAccountBook.accountBookName).isEqualTo(accountBook.accountBookName)
        Assertions.assertThat(findAccountBook.accountBookDesc).isEqualTo(accountBook.accountBookDesc)
        Assertions.assertThat(findAccountBook.accountRole).isEqualTo(accountBookUser.accountRole)
        Assertions.assertThat(findAccountBook.color).isEqualTo(accountBookUser.color)
        Assertions.assertThat(findAccountBook.backgroundColor).isEqualTo(accountBookUser.backGroundColor)
        Assertions.assertThat(
            findAccountBook.createdAt.format(
                DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")
            )
        )
            .isEqualTo(
                accountBookUser.createdAt.format(
                    DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")
                )
            )
    }
}