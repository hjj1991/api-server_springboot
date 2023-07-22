package com.hjj.apiserver.dto.purchase.request

import com.hjj.apiserver.common.exception.PurchaseModifyByIncomeValidFailException
import com.hjj.apiserver.domain.purchase.PurchaseType
import java.time.LocalDate

data class PurchaseModifyRequest(
    val accountBookNo: Long,
    val cardNo: Long? = null,
    val categoryNo: Long? = null,
    val purchaseType: PurchaseType,
    val price: Int,
    val reason: String,
    val purchaseDate: LocalDate,
){

    fun validRequest(){
        if(purchaseType == PurchaseType.INCOME && (cardNo != null || categoryNo != null)){
            throw PurchaseModifyByIncomeValidFailException()
        }
    }
}