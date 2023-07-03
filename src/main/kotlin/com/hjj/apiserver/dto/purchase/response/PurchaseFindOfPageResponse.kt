package com.hjj.apiserver.dto.purchase.response

import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.domain.purchase.Purchase
import com.hjj.apiserver.domain.purchase.PurchaseType
import java.time.LocalDate

data class PurchaseFindOfPageResponse(
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

    data class PurchaseCategoryInfo(
        val parentCategoryNo: Long? = null,
        val categoryNo: Long,
        val parentCategoryName: String? = null,
        val categoryName: String,
        val categoryDesc: String,
        val categoryIcon: String,
    ){
        companion object{
            fun of(category: Category): PurchaseCategoryInfo {
                return PurchaseCategoryInfo(
                    parentCategoryNo = category.parentCategory?.categoryNo,
                    categoryNo = category.categoryNo!!,
                    parentCategoryName = category.parentCategory?.categoryName,
                    categoryName = category.categoryName,
                    categoryDesc = category.categoryDesc,
                    categoryIcon = category.categoryIcon,
                )
            }
        }
    }


    companion object{
        fun of(purchase: Purchase): PurchaseFindOfPageResponse{
            return PurchaseFindOfPageResponse(
                purchaseNo = purchase.purchaseNo!!,
                userNo = purchase.user.userNo!!,
                cardNo = purchase.card?.cardNo,
                accountBookNo = purchase.accountBook.accountBookNo!!,
                purchaseType = purchase.purchaseType,
                price = purchase.price,
                reason = purchase.reason,
                purchaseDate = purchase.purchaseDate,
                categoryInfo = purchase.category?.let(PurchaseCategoryInfo::of)
            )
        }
    }
}