package com.hjj.apiserver.service

import com.hjj.apiserver.repository.saving.SavingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class SavingServiceTest @Autowired constructor(
    private val savingRepository: SavingRepository,
) {

    fun findSavingsTest() {
        // given

        // when
        savingRepository.findSavingAll()
        // then
    }
}