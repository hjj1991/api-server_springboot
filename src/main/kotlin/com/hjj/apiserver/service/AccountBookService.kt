package com.hjj.apiserver.service

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.category.Category
import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
import com.hjj.apiserver.dto.accountbook.response.AccountBookDetailResponse
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse
import com.hjj.apiserver.repository.accountbook.AccountBookRepository
import com.hjj.apiserver.repository.accountbook.AccountBookUserRepository
import com.hjj.apiserver.repository.card.CardRepository
import com.hjj.apiserver.repository.category.CategoryRepository
import com.hjj.apiserver.repository.user.UserRepository
import org.springframework.data.repository.findByIdOrNull
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
    private val categoryRepository: CategoryRepository,
) {

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun addAccountBook(userNo: Long, request: AccountBookAddRequest){

        val newAccountBook = AccountBook(
            accountBookName = request.accountBookName,
            accountBookDesc = request.accountBookDesc,
        )

        accountBookRepository.save(newAccountBook)
        accountBookUserRepository.save(
            AccountBookUser(
                accountBook = newAccountBook,
                user = userRepository.getById(userNo),
                accountRole = AccountRole.OWNER,
                backGroundColor = request.backGroundColor,
                color = request.color,
            )
        )
        categoryService.addBasicCategory(newAccountBook)
    }

    fun findAccountBookDetail(accountBookNo:Long, userNo: Long): AccountBookDetailResponse {
        val accountBook = accountBookRepository.findAccountBookByAccountBookNo(accountBookNo)?:throw IllegalArgumentException()

        val categoryOptional = categoryRepository.findByIdOrNull(1)

        val categories = accountBook.categories.map {
            AccountBookDetailResponse.CategoryDetail(
                categoryNo = it.categoryNo,
                categoryName = it.categoryName,
                categoryIcon = it.categoryIcon,
                childCategories = it.childCategories.map { childCategory: Category ->
                    AccountBookDetailResponse.ChildrenCategory(
                        categoryNo = childCategory.categoryNo,
                        categoryName = childCategory.categoryName,
                        categoryIcon = childCategory.categoryIcon,
                        parentCategoryNo = childCategory.parentCategory!!.categoryNo
                    )
                }
            )
        }



        val accountBookDetailResponse = AccountBookDetailResponse(
            accountBookNo = accountBook.accountBookNo!!,
            accountBookName = accountBook.accountBookName,
            accountBookDesc = accountBook.accountBookDesc,
            createdDate = accountBook.createdDate,
            cards = cardRepository.findByUser_UserNoAndDeleteYn(userNo).map {
                AccountBookDetailResponse.CardDetail(
                    cardNo = it.cardNo!!,
                    cardName = it.cardName
                )
            },
            categories = categories
        )

        return accountBookDetailResponse
    }

    fun findAllAccountBook(userNo: Long): List<AccountBookFindAllResponse>{
        return accountBookUserRepository.findAllAccountBookByUserNo(userNo)
    }
}