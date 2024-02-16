package com.hjj.apiserver.dto.main.response

import com.hjj.apiserver.dto.deposit.response.DepositIntrRateDescLimit10
import com.hjj.apiserver.dto.saving.response.SavingIntrRateDescLimit10

class MainFindResponse(
    val deposits: List<DepositIntrRateDescLimit10> = listOf(),
    val savings: List<SavingIntrRateDescLimit10> = listOf(),
)
