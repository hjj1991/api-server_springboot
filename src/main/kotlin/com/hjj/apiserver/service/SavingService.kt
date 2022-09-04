package com.hjj.apiserver.service

import com.hjj.apiserver.dto.saving.response.SavingFindAllResponse
import com.hjj.apiserver.repository.saving.SavingRepository
import org.springframework.stereotype.Service

@Service
class SavingService(
    private val savingRepository: SavingRepository,
) {

    fun findSavings():List<SavingFindAllResponse>{
        return savingRepository.findSavingAll()
    }


}