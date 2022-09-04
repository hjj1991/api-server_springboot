package com.hjj.apiserver.dto.purchase.request

import com.hjj.apiserver.domain.purchase.PurchaseType
import java.time.LocalDate

data class PurchaseAddRequest(
    val accountBookNo: Long,
    val cardNo: Long? = null,
    val categoryNo: Long? = null,
    val purchaseType: PurchaseType,
    val price: Int,
    val reason: String = "",
    val purchaseDate: LocalDate,
) {

    fun validRequest(){
        /* 들어온 돈은 카테고리, 카드 정보가 있으면 안된다. */
        if(purchaseType == PurchaseType.INCOME && (categoryNo != null || cardNo != null)){
            throw IllegalArgumentException()
        }

    }
}