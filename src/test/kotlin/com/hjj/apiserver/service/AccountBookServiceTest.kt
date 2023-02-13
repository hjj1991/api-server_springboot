package com.hjj.apiserver.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.dto.category.request.CategoryAddRequest
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.repository.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.EntityManager

@SpringBootTest
@Transactional
class AccountBookServiceTest @Autowired constructor(
    private val accountBookService: AccountBookService,
    private val userRepository: UserRepository,
    private val accountBookUserRepository: AccountBookUserRepository,
    private val accountBookRepository: AccountBookRepository,
    private val categoryRepository: CategoryRepository,
    private val objectMapper: ObjectMapper,
    private val categoryService: CategoryService,
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
                "SET REFERENTIAL_INTEGRITY TRUE; ")
            .executeUpdate()
    }


    @Test
    @DisplayName("가계부가 정상 생성된다.")
    fun addAccountBookTest() {
        // given
        val request = AccountBookAddRequest(
            accountBookName = "가계부명",
            accountBookDesc = "가계부설명",
            backGroundColor = "#ffffff",
            color = "#000000",
        )
        val savedUser = userRepository.save(User(
            userId = "testUser",
            nickName = "닉네임",
            userEmail = "tester@test.co.kr"
        ))

        // when
        accountBookService.addAccountBook(savedUser.userNo!!, request)

        // then
        val accountBookUser = accountBookUserRepository.findFirstByUser_UserNo(savedUser.userNo!!)
        val accountBook = accountBookRepository.findByIdOrNull(accountBookUser.accountBook.accountBookNo)?:throw IllegalStateException()
        assertThat(accountBookUser.color).isEqualTo(request.color)
        assertThat(accountBookUser.backGroundColor).isEqualTo(request.backGroundColor)
        assertThat(accountBookUser.accountRole).isEqualTo(AccountRole.OWNER)
        assertThat(accountBook.accountBookName).isEqualTo(request.accountBookName)
        assertThat(accountBook.accountBookDesc).isEqualTo(request.accountBookDesc)
    }

    @Test
    @DisplayName("가계부가 상세 조회 된다.")
    fun findAccountBookDetailTest(){
        // given
        val request = AccountBookAddRequest(
            accountBookName = "가계부명",
            accountBookDesc = "가계부설명",
            backGroundColor = "#ffffff",
            color = "#000000",
        )
        val savedUser = userRepository.save(User(
            userId = "testUser",
            nickName = "닉네임",
            userEmail = "tester@test.co.kr"
        ))
        val savedAccountBook = accountBookService.addAccountBook(savedUser.userNo!!, request)
        val accountBookUser = accountBookUserRepository.findFirstByUser_UserNo(savedUser.userNo!!)
        categoryService.addCategory(savedUser.userNo!!, CategoryAddRequest(
            accountBookNo = savedAccountBook.accountBookNo!!,
            parentCategoryNo = 1,
            categoryName = "테스트카테고리입니다.",
            categoryDesc = "테스터야",
            categoryIcon = "테스트아이콘",
        ))
        categoryService.addCategory(savedUser.userNo!!, CategoryAddRequest(
            accountBookNo = savedAccountBook.accountBookNo!!,
            parentCategoryNo = 1,
            categoryName = "22테스트카테고리입니다.",
            categoryDesc = "22테스터야",
            categoryIcon = "22테스트아이콘",
        ))
        entityManager.clear()

        // when
        val accountBookDetail = accountBookService.findAccountBookDetail(accountBookUser.accountBook.accountBookNo!!, savedUser.userNo!!)?: throw IllegalStateException()

        println(objectMapper.writeValueAsString(accountBookDetail))
        // then
        assertThat(accountBookDetail.accountBookName).isEqualTo(request.accountBookName)
        assertThat(accountBookDetail.categories).hasSize(15)
        assertThat(accountBookDetail.cards).hasSize(0)
    }


}