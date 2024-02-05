package com.hjj.apiserver.service.impl

import com.hjj.apiserver.common.exception.AccountBookNotFoundException
import com.hjj.apiserver.common.exception.CardNotFoundException
import com.hjj.apiserver.common.exception.CategoryNotFoundException
import com.hjj.apiserver.common.exception.PurchaseNotFoundException
import com.hjj.apiserver.dto.purchase.request.PurchaseAddRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseFindOfPageRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseModifyRequest
import com.hjj.apiserver.dto.purchase.response.PurchaseAddResponse
import com.hjj.apiserver.dto.purchase.response.PurchaseDetailResponse
import com.hjj.apiserver.dto.purchase.response.PurchaseFindOfPageResponse
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.card.CardRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.repository.purchase.PurchaseRepository
import com.hjj.apiserver.adapter.out.persistence.user.UserRepository
import com.hjj.apiserver.util.CommonUtils
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PurchaseService(
    private val accountBookRepository: AccountBookRepository,
    private val categoryRepository: CategoryRepository,
    private val purchaseRepository: PurchaseRepository,
    private val userRepository: UserRepository,
    private val cardRepository: CardRepository,
) {

    @Transactional(readOnly = false)
    fun addPurchase(userNo: Long, request: PurchaseAddRequest): PurchaseAddResponse {
        val accountBook = accountBookRepository.findAccountBook(
            userNo = userNo,
            accountBookNo = request.accountBookNo,
        ) ?: throw AccountBookNotFoundException()

        val card = request.cardNo?.let { cardRepository.findByIdOrNull(it) ?: throw CardNotFoundException() }
        val category = request.categoryNo?.let {
            categoryRepository.findCategoryByAccountRole(
                categoryNo = it,
                accountBookNo = request.accountBookNo,
                userNo = userNo,
            ) ?: throw CategoryNotFoundException()
        }
        val purchase = request.toEntity(
            card, category, userRepository.getReferenceById(userNo),
            accountBookRepository.getReferenceById(accountBook.accountBookNo)
        )
        val savedPurchase = purchaseRepository.save(purchase)

        return PurchaseAddResponse.of(savedPurchase)
    }

    fun findPurchasesOfPage(request: PurchaseFindOfPageRequest, pageable: Pageable): Slice<PurchaseFindOfPageResponse> {
        val purchases = purchaseRepository.findPurchasePageCustom(
            request.startDate,
            request.endDate,
            request.accountBookNo,
            pageable
        )
        val purchaseFindOfPageResponseList = purchases.map(PurchaseFindOfPageResponse::of).toMutableList()
        val hasNext = purchaseFindOfPageResponseList.size > pageable.pageSize

        return SliceImpl(
            CommonUtils.getSlicePageResult(purchaseFindOfPageResponseList, pageable.pageSize),
            pageable,
            hasNext
        )
    }

    @Transactional(readOnly = false)
    fun removePurchase(userNo: Long, purchaseNo: Long) {
        purchaseRepository.findEntityGraphByUserEntity_UserNoAndPurchaseNoAndIsDeleteIsFalse(userNo, purchaseNo)?.delete()
            ?: throw PurchaseNotFoundException()
    }

    @Transactional(readOnly = false)
    fun modifyPurchase(userNo: Long, purchaseNo: Long, request: PurchaseModifyRequest) {
        val purchase =
            purchaseRepository.findEntityGraphByUserEntity_UserNoAndPurchaseNoAndIsDeleteIsFalse(userNo, purchaseNo)
                ?: throw PurchaseNotFoundException()

        val card = request.cardNo?.let {
            cardRepository.findByCardNoAndUserEntity_UserNoAndIsDeleteIsFalse(it, userNo) ?: throw CardNotFoundException()
        }
        val category = request.categoryNo?.let {
            categoryRepository.findCategoryByAccountRole(
                categoryNo = it,
                accountBookNo = request.accountBookNo,
                userNo = userNo,
            ) ?: throw CategoryNotFoundException()
        }

        purchase.updatePurchase(
            request = request,
            card = card,
            category = category
        )
    }

    fun findPurchase(userNo: Long, purchaseNo: Long): PurchaseDetailResponse {
        return purchaseRepository.findPurchase(userNo, purchaseNo) ?: throw PurchaseNotFoundException()
    }

}