package com.hjj.apiserver.dto.purchase.response

import com.hjj.apiserver.domain.purchase.PurchaseType
import java.time.LocalDate

class PurchaseFindOfPageResponse(
    val purchaseNo: Long,
    val userNo: Long,
    val cardNo: Long? = null,
    val accountBookNo: Long,
    val purchaseType: PurchaseType,
    val price: Int,
    val reason: String,
    val purchaseDate: LocalDate,
    val categoryInfo: PurchaseCategoryInfo? = null,

) {

    class PurchaseCategoryInfo(
        val parentCategoryNo: Long? = null,
        val categoryNo: Long,
        val parentCategoryName: String? = null,
        val categoryName: String,
        val categoryDesc: String,
        val categoryIcon: String,
    )
}