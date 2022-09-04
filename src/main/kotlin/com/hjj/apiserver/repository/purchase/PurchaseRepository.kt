package com.hjj.apiserver.repository.purchase

import com.hjj.apiserver.domain.purchase.Purchase
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface PurchaseRepository: JpaRepository<Purchase, Long>, PurchaseRepositoryCustom {

    @EntityGraph(attributePaths = ["card", "category"])
    fun findAllEntityGraphByPurchaseDateBetweenAndAccountBook_AccountBookNoAndDeleteYnOrderByPurchaseDateDesc(
        startDate: LocalDate,
        endDate: LocalDate,
        accountBookNo: Long,
        deleteYn: Char
    ): List<Purchase>

    @EntityGraph(attributePaths = ["card", "category"])
    fun findAllEntityGraphByPurchaseDateBetweenAndUser_UserNoOrderByPurchaseDateDesc(
        startDate: LocalDate,
        endDate: LocalDate,
        userNo: Long
    ): List<Purchase>

    @EntityGraph(attributePaths = ["user"])
    fun findEntityGraphByUser_UserNoAndPurchaseNoAndDeleteYn(
        userNo: Long,
        purchaseNo: Long,
        deleteYn: Char = 'N',
    ): Purchase?

    fun findByCategory_CategoryNo(categoryNo: Long): List<Purchase>

    @Modifying(clearAutomatically = true)
    @Query("update Purchase set category = null where category.categoryNo = :categoryNo")
    fun deleteCategoryAllPurchaseByCategoryNo(@Param("categoryNo") categoryNo: Long)

}