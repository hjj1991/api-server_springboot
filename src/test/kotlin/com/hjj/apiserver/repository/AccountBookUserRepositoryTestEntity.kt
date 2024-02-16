package com.hjj.apiserver.repository

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import org.assertj.core.api.Assertions
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class AccountBookUserRepositoryTestEntity : BaseRepositoryTest() {
    @Autowired
    private lateinit var accountBookUserRepository: AccountBookUserRepository

    @Autowired
    private lateinit var accountBookRepository: AccountBookRepository

    @Test
    @DisplayName("사용자가 속한 모든 가계부를 조회 성공한다.")
    fun findAllAccountBookByUserNoTest_success() {
        // given
        val tester1 = createUser("tester1", "테스터1")
        val tester2 = createUser("tester2", "테스터2")
        val tester3 = createUser("tester3", "테스터3")

        val accountBook1 =
            accountBookRepository.save(AccountBook(accountBookName = "testBookName", accountBookDesc = "testBookDesc"))

        val accountBookUser1 =
            accountBookUserRepository.save(
                AccountBookUser(
                    accountBook = accountBook1,
                    userEntity = tester1,
                    accountRole = AccountRole.OWNER,
                    backGroundColor = "#00000",
                    color = "#11111",
                ),
            )
        accountBookUserRepository.save(
            AccountBookUser(
                accountBook = accountBook1,
                userEntity = tester2,
                accountRole = AccountRole.MEMBER,
                backGroundColor = "#00123",
                color = "#11789",
            ),
        )
        accountBookUserRepository.save(
            AccountBookUser(
                accountBook = accountBook1,
                userEntity = tester3,
                accountRole = AccountRole.GUEST,
                backGroundColor = "#00456",
                color = "#11345",
            ),
        )

        val accountBook2 =
            accountBookRepository.save(
                AccountBook(
                    accountBookName = "testBookName2",
                    accountBookDesc = "testBookDesc2",
                ),
            )

        val accountBookUser2 =
            accountBookUserRepository.save(
                AccountBookUser(
                    accountBook = accountBook2,
                    userEntity = tester1,
                    accountRole = AccountRole.OWNER,
                    backGroundColor = "#33000",
                    color = "#55511",
                ),
            )

        // when
        val findAllAccountBookByUserNo = accountBookUserRepository.findAllAccountBookByUserNo(tester1.userNo!!)

        // then
        Assertions.assertThat(findAllAccountBookByUserNo.size).isEqualTo(2)
        Assertions.assertThat(findAllAccountBookByUserNo[0].accountBookNo).isEqualTo(accountBook1.accountBookNo)
        Assertions.assertThat(findAllAccountBookByUserNo[0].accountBookName).isEqualTo(accountBook1.accountBookName)
        Assertions.assertThat(findAllAccountBookByUserNo[0].accountBookDesc).isEqualTo(accountBook1.accountBookDesc)
        Assertions.assertThat(findAllAccountBookByUserNo[0].accountRole).isEqualTo(accountBookUser1.accountRole)
        Assertions.assertThat(findAllAccountBookByUserNo[0].backGroundColor).isEqualTo(accountBookUser1.backGroundColor)
        Assertions.assertThat(findAllAccountBookByUserNo[0].color).isEqualTo(accountBookUser1.color)
        Assertions.assertThat(findAllAccountBookByUserNo[0].joinedUsers.size).isEqualTo(3)
        Assertions.assertThat(findAllAccountBookByUserNo[0].joinedUsers)
            .extracting(AccountBookFindAllResponse.JoinedUser::userNo, AccountBookFindAllResponse.JoinedUser::nickName)
            .contains(
                Tuple.tuple(tester1.userNo, tester1.nickName),
                Tuple.tuple(tester2.userNo, tester2.nickName),
                Tuple.tuple(tester3.userNo, tester3.nickName),
            )
        Assertions.assertThat(findAllAccountBookByUserNo[1].accountBookNo).isEqualTo(accountBook2.accountBookNo)
        Assertions.assertThat(findAllAccountBookByUserNo[1].accountBookName).isEqualTo(accountBook2.accountBookName)
        Assertions.assertThat(findAllAccountBookByUserNo[1].accountBookDesc).isEqualTo(accountBook2.accountBookDesc)
        Assertions.assertThat(findAllAccountBookByUserNo[1].accountRole).isEqualTo(accountBookUser2.accountRole)
        Assertions.assertThat(findAllAccountBookByUserNo[1].backGroundColor).isEqualTo(accountBookUser2.backGroundColor)
        Assertions.assertThat(findAllAccountBookByUserNo[1].color).isEqualTo(accountBookUser2.color)
        Assertions.assertThat(findAllAccountBookByUserNo[1].joinedUsers.size).isEqualTo(1)
    }

    @Test
    @DisplayName("가계부의 해당사용자의 권한 조회가 정상 성공한다.")
    fun findAccountRole_success() {
        // given
        val tester1 = createUser("tester1", "테스터1")
        val accountBook1 =
            accountBookRepository.save(AccountBook(accountBookName = "testBookName", accountBookDesc = "testBookDesc"))
        val accountBookUser1 =
            accountBookUserRepository.save(
                AccountBookUser(
                    accountBook = accountBook1,
                    userEntity = tester1,
                    accountRole = AccountRole.OWNER,
                    backGroundColor = "#00000",
                    color = "#11111",
                ),
            )

        // when
        val findAccountRole = accountBookUserRepository.findAccountRole(tester1.userNo!!, accountBook1.accountBookNo!!)
        Assertions.assertThat(findAccountRole).isEqualTo(accountBookUser1.accountRole)
    }
}
