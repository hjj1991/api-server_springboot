package com.hjj.apiserver.repository.deposit

import com.hjj.apiserver.dto.deposit.response.DepositFindAllResponse
import com.hjj.apiserver.dto.deposit.response.DepositIntrRateDescLimit10

interface DepositRepositoryCustom {
    fun findDepositAll(): List<DepositFindAllResponse>
    fun findDepositByHome(): List<DepositIntrRateDescLimit10>
}