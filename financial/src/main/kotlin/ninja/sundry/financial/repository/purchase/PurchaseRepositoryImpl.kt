package com.hjj.apiserver.repository.purchase

import com.hjj.apiserver.domain.card.QCard.Companion.card
import com.hjj.apiserver.domain.category.QCategory.Companion.category
import com.hjj.apiserver.domain.purchase.Purchase
import com.hjj.apiserver.domain.purchase.QPurchase.Companion.purchase
import com.hjj.apiserver.dto.purchase.response.PurchaseDetailResponse
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import java.time.LocalDate

class PurchaseRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : PurchaseRepositoryCustom {
    override fun findPurchase(
        userNo: Long,
        purchaseNo: Long,
    ): PurchaseDetailResponse? {
        return jpaQueryFactory
            .select(
                Projections.constructor(
                    PurchaseDetailResponse::class.java,
                    purchase.purchaseNo,
                    purchase.accountBook.accountBookNo,
                    purchase.card.cardNo,
                    category.categoryNo,
                    category.parentCategory.categoryNo,
                    purchase.purchaseType,
                    purchase.price,
                    purchase.reason,
                    purchase.purchaseDate,
                ),
            )
            .from(purchase)
            .leftJoin(purchase.category, category)
            .where(purchase.purchaseNo.eq(purchaseNo).and(purchase.userEntity.userNo.eq(userNo)))
            .fetchOne()
    }

    override fun findPurchasePageCustom(
        searchStartDate: LocalDate,
        searchEndDate: LocalDate,
        accountBookNo: Long,
        pageable: Pageable,
    ): List<Purchase> {
        return jpaQueryFactory
            .select(purchase)
            .from(purchase)
            .leftJoin(purchase.card, card).fetchJoin()
            .leftJoin(purchase.category, category).fetchJoin()
            .where(
                purchase.purchaseDate.between(searchStartDate, searchEndDate)
                    .and(purchase.accountBook.accountBookNo.eq(accountBookNo))
                    .and(purchase.isDelete.isFalse),
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize + 1L)
            .orderBy(purchase.purchaseDate.desc(), purchase.purchaseNo.desc())
            .fetch()
    }
}
