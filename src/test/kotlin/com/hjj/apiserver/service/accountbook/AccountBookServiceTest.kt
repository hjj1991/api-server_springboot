package com.hjj.apiserver.service.accountbook

import com.hjj.apiserver.service.AccountBookService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AccountBookServiceTest @Autowired constructor(
    private val accountBookService: AccountBookService,
) {

    @Test
    @DisplayName("해당 유저의 모든 가게부 정보 조회")
    fun findAllAccountBookTest(){
        //given
        val userNo = 1L

        //when
        val findAllAccountBook = accountBookService.findAllAccountBook(1)

        //then
        println(findAllAccountBook)

    }
}