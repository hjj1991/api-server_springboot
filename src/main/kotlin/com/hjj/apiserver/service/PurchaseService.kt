package com.hjj.apiserver.service

import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.purchase.Purchase
import com.hjj.apiserver.dto.purchase.request.PurchaseAddRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseFindOfPageRequest
import com.hjj.apiserver.dto.purchase.request.PurchaseModifyRequest
import com.hjj.apiserver.dto.purchase.response.PurchaseDetailResponse
import com.hjj.apiserver.dto.purchase.response.PurchaseFindOfPageResponse
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.card.CardRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.repository.purchase.PurchaseRepository
import com.hjj.apiserver.repository.user.UserRepository
import com.hjj.apiserver.util.CommonUtils
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
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

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun addPurchase(userNo: Long, request: PurchaseAddRequest): Purchase {
        val accountBook = accountBookRepository.findAccountBook(
            userNo = userNo,
            accountBookNo = request.accountBookNo,
        ) ?: throw IllegalArgumentException()

        request.validRequest()


        return purchaseRepository.save(
            Purchase(
                purchaseType = request.purchaseType,
                price = request.price,
                reason = request.reason,
                purchaseDate = request.purchaseDate,
                card = request.cardNo?.let {
                    cardRepository.getById(it) ?: throw IllegalArgumentException()
                },
                category = request.categoryNo?.let {
                    categoryRepository.findCategoryByAccountRole(
                        request.categoryNo,
                        request.accountBookNo,
                        userNo,
                        setOf(AccountRole.OWNER, AccountRole.MEMBER)
                    ) ?: throw IllegalArgumentException()
                },
                user = userRepository.getReferenceById(userNo),
                accountBook = accountBookRepository.getReferenceById(accountBook.accountBookNo),
            )
        )
    }

    fun findPurchasesOfPage(request: PurchaseFindOfPageRequest, pageable: Pageable): Slice<PurchaseFindOfPageResponse> {
        val purchases = purchaseRepository.findPurchasePageCustom(
            request.startDate,
            request.endDate,
            request.accountBookNo,
            pageable
        )

        val purchaseFindOfPageResponseList = purchases.map { purchase ->
            PurchaseFindOfPageResponse(
                purchaseNo = purchase.purchaseNo!!,
                userNo = purchase.user.userNo!!,
                cardNo = purchase.card?.cardNo,
                accountBookNo = purchase.accountBook.accountBookNo!!,
                purchaseType = purchase.purchaseType,
                price = purchase.price,
                reason = purchase.reason,
                purchaseDate = purchase.purchaseDate,
                categoryInfo = purchase.category?.let {
                    PurchaseFindOfPageResponse.PurchaseCategoryInfo(
                        parentCategoryNo = it.parentCategory?.categoryNo,
                        categoryNo = it.categoryNo!!,
                        parentCategoryName = it.parentCategory?.categoryName,
                        categoryName = it.categoryName,
                        categoryDesc = it.categoryDesc,
                        categoryIcon = it.categoryIcon,
                    )
                }
            )
        }.toMutableList()

        val hasNext = purchaseFindOfPageResponseList.size > pageable.pageSize
        return SliceImpl(
            CommonUtils.getSlicePageResult(purchaseFindOfPageResponseList, pageable.pageSize),
            pageable,
            hasNext
        )
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun removePurchase(userNo: Long, purchaseNo: Long) {
        purchaseRepository.findEntityGraphByUser_UserNoAndPurchaseNoAndIsDeleteIsFalse(userNo, purchaseNo)?.delete()
            ?: throw IllegalArgumentException()
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun modifyPurchase(userNo: Long, purchaseNo: Long, request: PurchaseModifyRequest) {
        val purchase = purchaseRepository.findEntityGraphByUser_UserNoAndPurchaseNoAndIsDeleteIsFalse(userNo, purchaseNo)
            ?: throw IllegalArgumentException()

        request.validRequest()

        purchase.updatePurchase(
            request = request,
            card = cardRepository.findByCardNoAndUser_UserNoAndIsDeleteIsFalse(request.cardNo ?: 0, userNo),
            category = categoryRepository.findCategoryByAccountRole(
                categoryNo = request.categoryNo ?: 0,
                accountBookNo = request.accountBookNo,
                userNo = userNo,
                accountRoles = setOf(AccountRole.MEMBER, AccountRole.OWNER)
            ),
        )
    }

    fun findPurchase(userNo: Long, purchaseNo: Long): PurchaseDetailResponse {
        return purchaseRepository.findPurchase(userNo, purchaseNo) ?: throw IllegalArgumentException()
    }

}