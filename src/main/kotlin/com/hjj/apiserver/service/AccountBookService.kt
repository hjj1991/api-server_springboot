package com.hjj.apiserver.service

import com.hjj.apiserver.domain.accountbook.AccountBook
import com.hjj.apiserver.domain.accountbook.AccountBookUser
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.dto.accountbook.request.AccountBookAddRequest
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

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun addAccountBook(userNo: Long, request: AccountBookAddRequest){

        val newAccountBook = AccountBook(
            accountBookName = request.accountBookName,
            accountBookDesc = request.accountBookDesc
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

    fun findAccountBookDetail(accountBookNo:Long, userNo: Long): AccountBookDetailResponse? {
        val accountBookDetail = accountBookRepository.findAccountBookDetail(accountBookNo)
        accountBookDetail?.apply {
            val selectCards = cardRepository.findByUser_UserNoAndDeleteYn(userNo)
            this.cards = selectCards.map { AccountBookDetailResponse.CardDetail(it.cardNo!!, it.cardName) }
        }

        return accountBookDetail
    }

    fun findAllAccountBook(userNo: Long): List<AccountBookFindAllResponse>{
        return accountBookUserRepository.findAllAccountBookByUserNo(userNo)
    }
}