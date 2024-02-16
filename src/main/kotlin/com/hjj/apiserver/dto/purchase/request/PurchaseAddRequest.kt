package com.hjj.apiserver.dto.purchase.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.card.Card
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.domain.purchase.Purchase
import com.hjj.apiserver.domain.purchase.PurchaseType
import com.hjj.apiserver.util.EnumValue
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDate

data class PurchaseAddRequest(
    val accountBookNo: Long,
    val cardNo: Long? = null,
    val categoryNo: Long? = null,
    @field:EnumValue(enumClass = PurchaseType::class, ignoreCase = true)
    val purchaseType: PurchaseType,
    @field:PositiveOrZero
    val price: Int,
    val reason: String = "",
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val purchaseDate: LocalDate,
) {
    fun validRequest() {
        // 들어온 돈은 카테고리, 카드 정보가 있으면 안된다.
        if (purchaseType == PurchaseType.INCOME && (categoryNo != null || cardNo != null)) {
            throw IllegalArgumentException()
        }
    }

    fun toEntity(
        card: Card?,
        category: Category?,
        userEntity: UserEntity,
        accountBook: AccountBook,
    ): Purchase {
        return Purchase(
            purchaseType = purchaseType,
            price = price,
            reason = reason,
            purchaseDate = purchaseDate,
            card = card,
            category = category,
            userEntity = userEntity,
            accountBook = accountBook,
        )
    }
}
