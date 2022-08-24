package com.hjj.apiserver.service

import com.hjj.apiserver.dto.deposit.response.DepositFindAllResponse
import com.hjj.apiserver.repository.deposit.DepositRepository
import org.springframework.stereotype.Service

@Service
class DepositService(
    private val depositRepository: DepositRepository,
) {

    fun findDepositList():List<DepositFindAllResponse> {
        return depositRepository.findDepositAll()
    }
}