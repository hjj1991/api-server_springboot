package com.hjj.apiserver.repository.purchase

import com.hjj.apiserver.domain.purchase.Purchase
import com.hjj.apiserver.dto.purchase.response.PurchaseDetailResponse
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface PurchaseRepositoryCustom {
    fun findPurchase(
        userNo: Long,
        purchaseNo: Long,
    ): PurchaseDetailResponse?

    fun findPurchasePageCustom(
        searchStartDate: LocalDate,
        searchEndDate: LocalDate,
        accountBookNo: Long,
        pageable: Pageable,
    ): List<Purchase>
}
