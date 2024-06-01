package com.hjj.apiserver.repository.saving

import com.hjj.apiserver.dto.saving.response.SavingFindAllResponse
import com.hjj.apiserver.dto.saving.response.SavingIntrRateDescLimit10

interface SavingRepositoryCustom {
    fun findSavingAll(): List<SavingFindAllResponse>

    fun findSavingByHome(): List<SavingIntrRateDescLimit10>
}
