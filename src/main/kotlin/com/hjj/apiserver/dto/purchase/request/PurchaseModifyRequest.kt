package com.hjj.apiserver.dto.purchase.request

import com.hjj.apiserver.domain.purchase.PurchaseType
import java.time.LocalDate

class PurchaseModifyRequest(
    var purchaseNo: Long = 0,
    val accountBookNo: Long,
    val cardNo: Long,
    val categoryNo: Long? = null,
    val purchaseType: PurchaseType,
    val price: Int,
    val reason: String,
    val purchaseDate: LocalDate,
)