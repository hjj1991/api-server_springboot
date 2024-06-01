package com.hjj.apiserver.dto.purchase.response

import com.hjj.apiserver.domain.purchase.Purchase
import com.hjj.apiserver.domain.purchase.PurchaseType
import java.time.LocalDate

data class PurchaseAddResponse(
    val purchaseNo: Long,
    val accountBookNo: Long,
    val cardNo: Long? = null,
    val categoryNo: Long? = null,
    val purchaseType: PurchaseType,
    val price: Int,
    val reason: String = "",
    val purchaseDate: LocalDate,
) {
    companion object {
        fun of(purchase: Purchase): PurchaseAddResponse {
            return PurchaseAddResponse(
                purchaseNo = purchase.purchaseNo!!,
                accountBookNo = purchase.accountBook.accountBookNo!!,
                cardNo = purchase.card?.cardNo,
                categoryNo = purchase.category?.categoryNo,
                purchaseType = purchase.purchaseType,
                price = purchase.price,
                reason = purchase.reason,
                purchaseDate = purchase.purchaseDate,
            )
        }
    }
}
