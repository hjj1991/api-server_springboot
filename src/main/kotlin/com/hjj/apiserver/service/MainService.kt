package com.hjj.apiserver.service

import com.hjj.apiserver.dto.main.response.MainFindResponse
import com.hjj.apiserver.repository.deposit.DepositRepository
import com.hjj.apiserver.repository.saving.SavingRepository
import org.springframework.stereotype.Service

@Service
class MainService(
    private val depositRepository: DepositRepository,
    private val savingRepository: SavingRepository,
) {

    fun findMain():MainFindResponse{
        return MainFindResponse(
            deposits = depositRepository.findDepositByHome(),
            savings = savingRepository.findSavingByHome(),
        )
    }
}