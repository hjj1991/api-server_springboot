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
import org.modelmapper.ModelMapper
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
    private val modelMapper: ModelMapper,
) {

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun addPurchase(userNo: Long, request: PurchaseAddRequest) {
        val accountBook = accountBookRepository.findAccountBookBySubQuery(userNo, request.accountBookNo)
            ?: throw IllegalArgumentException()


        purchaseRepository.save(
            Purchase(
                purchaseType = request.purchaseType,
                price = request.price,
                reason = request.reason,
                purchaseDate = request.purchaseDate,
                card = request.cardNo?.let {
                    cardRepository.getById(request.cardNo) ?: throw IllegalArgumentException()
                },
                category = request.categoryNo?.let {
                    categoryRepository.findByCategoryNoAndSubQuery(
                        request.categoryNo,
                        request.accountBookNo,
                        userNo,
                        listOf(AccountRole.OWNER, AccountRole.MEMBER)
                    ) ?: throw IllegalArgumentException()
                },
                user = userRepository.getById(userNo),
                accountBook = accountBook,
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

        return SliceImpl(purchaseFindOfPageResponseList.subList(0, pageable.pageSize - 1), pageable, hasNext)
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun deletePurchase(userNo: Long, purchaseNo: Long) {
        purchaseRepository.findEntityGraphByUser_UserNoAndPurchaseNoAndDeleteYn(userNo, purchaseNo)?.delete()
            ?: throw IllegalArgumentException()
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun modifyPurchase(userNo: Long, purchaseNo: Long, request: PurchaseModifyRequest) {
        val purchase = purchaseRepository.findEntityGraphByUser_UserNoAndPurchaseNoAndDeleteYn(userNo, purchaseNo)
            ?: throw IllegalArgumentException()

        purchase.updatePurchase(
            request = request,
            card = if (request.cardNo != purchase.card?.cardNo ?: null) {
                cardRepository.findByCardNoAndUser_UserNo(request.cardNo?: 0, userNo)
            } else {
                purchase.card
            },
            category = if(request.categoryNo != purchase.category?.categoryNo?: null){
                categoryRepository.findByCategoryNoAndSubQuery(request.categoryNo?: 0, request.accountBookNo, userNo, listOf(AccountRole.OWNER))
            }else {
                purchase.category
            }
        )
    }

    fun findPurchase(userNo: Long, purchaseNo: Long): PurchaseDetailResponse {
        return purchaseRepository.findPurchase(userNo, purchaseNo) ?: throw IllegalArgumentException()
    }

}