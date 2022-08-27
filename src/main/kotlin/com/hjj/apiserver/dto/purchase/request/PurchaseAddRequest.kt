package com.hjj.apiserver.dto.purchase.request

import com.hjj.apiserver.domain.purchase.PurchaseType
import java.time.LocalDate

data class PurchaseAddRequest(
    val accountBookNo: Long,
    val cardNo: Long,
    val categoryNo: Long,
    val purchaseType: PurchaseType,
    val price: Int,
    val reason: String = "",
    val purchaseDate: LocalDate,
) {
}