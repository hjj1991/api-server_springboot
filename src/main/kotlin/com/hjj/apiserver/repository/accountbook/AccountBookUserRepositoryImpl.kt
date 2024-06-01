package com.hjj.apiserver.repository.accountbook

import com.hjj.apiserver.adapter.out.persistence.user.QUserEntity.Companion.userEntity
import com.hjj.apiserver.domain.accountbook.AccountRole
import com.hjj.apiserver.domain.accountbook.QAccountBook.Companion.accountBook
import com.hjj.apiserver.domain.accountbook.QAccountBookUser
import com.hjj.apiserver.domain.accountbook.QAccountBookUser.Companion.accountBookUser
import com.hjj.apiserver.dto.accountbook.response.AccountBookFindAllResponse
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory

class AccountBookUserRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : AccountBookUserRepositoryCustom {
    override fun findAllAccountBookByUserNo(userNo: Long): List<AccountBookFindAllResponse> {
        val subABU = QAccountBookUser("subABU")
        return jpaQueryFactory
            .selectFrom(accountBookUser)
            .innerJoin(accountBookUser.accountBook, accountBook)
            .leftJoin(subABU)
            .on(accountBookUser.accountBook.accountBookNo.eq(subABU.accountBook.accountBookNo))
            .innerJoin(subABU.userEntity, userEntity)
            .where(accountBookUser.userEntity.userNo.eq(userNo))
            .orderBy(accountBookUser.accountBook.accountBookNo.asc())
            .transform(
                groupBy(
                    accountBookUser.accountBook.accountBookNo,
                    accountBookUser.accountBook.accountBookName,
                    accountBookUser.accountBook.accountBookDesc,
                    accountBookUser.backGroundColor,
                    accountBookUser.color,
                    accountBookUser.accountRole,
                ).list(
                    Projections.constructor(
                        AccountBookFindAllResponse::class.java,
                        accountBookUser.accountBook.accountBookNo,
                        accountBookUser.accountBook.accountBookName,
                        accountBookUser.accountBook.accountBookDesc,
                        accountBookUser.backGroundColor,
                        accountBookUser.color,
                        accountBookUser.accountRole,
                        list(
                            Projections.constructor(
                                AccountBookFindAllResponse.JoinedUser::class.java,
                                userEntity.userNo,
                                userEntity.nickName,
                                userEntity.picture,
                            ),
                        ),
                    ),
                ),
            )
    }

    override fun findAccountRole(
        userNo: Long,
        accountBookNo: Long,
    ): AccountRole? {
        return jpaQueryFactory.select(accountBookUser.accountRole)
            .from(accountBookUser)
            .where(
                accountBookUser.userEntity.userNo.eq(userNo),
                accountBookUser.accountBook.accountBookNo.eq(accountBookNo),
            ).fetchOne()
    }
}
