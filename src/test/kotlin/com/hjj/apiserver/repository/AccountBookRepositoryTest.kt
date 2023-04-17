package com.hjj.apiserver.repository

import com.hjj.apiserver.config.DataSourceConfiguration
import com.hjj.apiserver.config.TestConfiguration
import com.hjj.apiserver.config.TestMariaDBContainer
import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.repository.user.UserRepository
import com.hjj.apiserver.util.CommonUtils
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(TestMariaDBContainer::class, DataSourceConfiguration::class, TestConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountBookRepositoryTest {

    @Autowired
    private lateinit var accountBookRepository: AccountBookRepository

    @Autowired
    private lateinit var accountBookUserRepository: AccountBookUserRepository

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var userRepository: UserRepository


    @Test
    @DisplayName("사용자의 가계부가 정상 조회된다.")
    fun findAccountBookTest_success(){
        // given
        val user = User(
            userId = "userId",
            nickName = "nickName",
            role = Role.USER
        )
        val testUser = userRepository.save(user)

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
        val findAccountBook = accountBookRepository.findAccountBook(testUser.userNo!!, accountBook.accountBookNo!!)

        // then
        Assertions.assertThat(findAccountBook).isNotNull
        Assertions.assertThat(findAccountBook!!.accountBookNo).isEqualTo(accountBook.accountBookNo)


    }
}