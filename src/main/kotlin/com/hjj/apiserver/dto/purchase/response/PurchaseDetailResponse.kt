package com.hjj.apiserver.dto.purchase.response

import com.hjj.apiserver.domain.purchase.PurchaseType
import java.time.LocalDate

data class PurchaseDetailResponse(
    val accountBookNo: Long,
    val purchaseNo: Long,
    val cardNo: Long? = null,
    val categoryNo: Long? = null,
    val parentCategoryNo: Long? = null,
    val purchaseType: PurchaseType,
    val price: Int,
    val reason: String,
    val purchaseDate: LocalDate,
) {
}