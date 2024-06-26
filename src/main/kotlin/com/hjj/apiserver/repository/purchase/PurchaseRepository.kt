package com.hjj.apiserver.repository.purchase

import com.hjj.apiserver.domain.purchase.Purchase
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface PurchaseRepository : JpaRepository<Purchase, Long>, PurchaseRepositoryCustom {
    @EntityGraph(attributePaths = ["card", "category"])
    fun findAllEntityGraphByPurchaseDateBetweenAndUserEntityUserNoOrderByPurchaseDateDesc(
        startDate: LocalDate,
        endDate: LocalDate,
        userNo: Long,
    ): List<Purchase>

    @EntityGraph(attributePaths = ["user"])
    fun findEntityGraphByUserEntityUserNoAndPurchaseNoAndIsDeleteIsFalse(
        userNo: Long,
        purchaseNo: Long,
    ): Purchase?

    fun findByCategoryCategoryNo(categoryNo: Long): List<Purchase>

    @Modifying(clearAutomatically = true)
    @Query("update Purchase set category = null where category.categoryNo = :categoryNo")
    fun deleteCategoryAllPurchaseByCategoryNo(
        @Param("categoryNo") categoryNo: Long,
    )
}
