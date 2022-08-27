package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.domain.accountbook.QAccountBook.accountBook
import com.hjj.apiserver.domain.accountbook.QAccountBookUser
import com.hjj.apiserver.domain.accountbook.QAccountBookUser.accountBookUser
import com.hjj.apiserver.domain.user.QUser.user
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class AccountBookUserRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
): AccountBookUserRepositoryCustom {
    override fun findAllAccountBookByUserNo(userNo: Long): List<AccountBookFindAllResponse> {
        val subABU = QAccountBookUser("subABU")
        return jpaQueryFactory
            .selectFrom(accountBookUser)
            .innerJoin(accountBookUser.accountBook, accountBook)
            .leftJoin(subABU)
            .on(accountBookUser.accountBook.accountBookNo.eq(subABU.accountBook.accountBookNo))
            .innerJoin(subABU.user, user)
            .where(accountBookUser.user.userNo.eq(userNo))
            .distinct()
            .transform(groupBy(
                accountBook.accountBookNo,
                accountBook.accountBookName,
                accountBook.accountBookDesc,
                accountBookUser.backGroundColor,
                accountBookUser.color,
                accountBookUser.accountRole,
            ).list(
                    Projections.constructor(
                        AccountBookFindAllResponse::class.java,
                        accountBook.accountBookNo,
                        accountBook.accountBookName,
                        accountBook.accountBookDesc,
                        accountBookUser.backGroundColor,
                        accountBookUser.color,
                        accountBookUser.accountRole,
                        list(
                            Projections.constructor(
                                AccountBookFindAllResponse.JoinedUser::class.java,
                                user.userNo,
                                user.nickName,
                                user.picture
                            )
                        )
                    )
                )
            )
    }
}