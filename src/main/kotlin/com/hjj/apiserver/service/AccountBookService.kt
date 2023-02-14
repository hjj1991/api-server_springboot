package com.hjj.apiserver.service

import com.hjj.apiserver.common.exception.AccountBookNotFoundException
import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.dto.accountbook.response.AccountBookAddResponse
import com.hjj.apiserver.dto.accountbook.response.AccountBookDetailResponse
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.card.CardRepository
import com.hjj.apiserver.repository.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AccountBookService(
    private val accountBookUserRepository: AccountBookUserRepository,
    private val accountBookRepository: AccountBookRepository,
    private val categoryService: CategoryService,
    private val cardRepository: CardRepository,
    private val userRepository: UserRepository,
) {

    @Transactional(readOnly = false)
    fun addAccountBook(userNo: Long, request: AccountBookAddRequest): AccountBookAddResponse {
        val newAccountBook = AccountBook(
            accountBookName = request.accountBookName,
            accountBookDesc = request.accountBookDesc,
        )
        accountBookRepository.save(newAccountBook)

        val savedAccountBookUser = accountBookUserRepository.save(
            AccountBookUser(
                accountBook = newAccountBook,
                user = userRepository.getReferenceById(userNo),
                accountRole = AccountRole.OWNER,
                backGroundColor = request.backGroundColor,
                color = request.color,
            )
        )
        categoryService.addBasicCategory(newAccountBook)
        return AccountBookAddResponse.of(savedAccountBookUser)
    }

    fun findAccountBookDetail(accountBookNo: Long, userNo: Long): AccountBookDetailResponse {
        val accountBook =
            accountBookRepository.findAccountBook(userNo, accountBookNo, listOf(AccountRole.MEMBER, AccountRole.OWNER))
                ?: throw AccountBookNotFoundException()

        val categories: MutableList<AccountBookDetailResponse.CategoryDetail> = mutableListOf()

        for (category in accountBook.categories) {
            if (category.parentCategory == null) {
                categories.add(AccountBookDetailResponse.CategoryDetail(
                    categoryNo = category.categoryNo,
                    categoryName = category.categoryName,
                    categoryIcon = category.categoryIcon,
                    childCategories = category.childCategories.map { childCategory: Category ->
                        AccountBookDetailResponse.ChildrenCategory(
                            categoryNo = childCategory.categoryNo,
                            categoryName = childCategory.categoryName,
                            categoryIcon = childCategory.categoryIcon,
                            parentCategoryNo = childCategory.parentCategory!!.categoryNo
                        )
                    }
                )
                )
            }
        }


        return AccountBookDetailResponse(
            accountBookNo = accountBook.accountBookNo!!,
            accountBookName = accountBook.accountBookName,
            accountBookDesc = accountBook.accountBookDesc,
            accountRole = accountBook.accountBookUserList.find { it.user.userNo == userNo }!!.accountRole,
            createdDate = accountBook.createdDate,
            cards = cardRepository.findByUser_UserNoAndDeleteYn(userNo).map {
                AccountBookDetailResponse.CardDetail(
                    cardNo = it.cardNo!!,
                    cardName = it.cardName
                )
            },
            categories = categories
        )
    }

    fun findAllAccountBook(userNo: Long): List<AccountBookFindAllResponse> {
        return accountBookUserRepository.findAllAccountBookByUserNo(userNo)
    }
}