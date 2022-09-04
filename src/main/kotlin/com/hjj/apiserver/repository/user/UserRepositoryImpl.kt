package com.hjj.apiserver.repository.user

import com.hjj.apiserver.domain.user.QUser.user
import com.hjj.apiserver.domain.user.QUserLog.userLog
import com.hjj.apiserver.dto.user.response.UserDetailResponse
import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory

class UserRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : UserRepositoryCustom {
    override fun findUserDetail(userNo: Long): UserDetailResponse? {
        return jpaQueryFactory.select(
            Projections.constructor(
                UserDetailResponse::class.java,
                user.userNo,
                user.userId,
                user.nickName,
                user.userEmail,
                user.provider,
                user.role,
                user.providerConnectDate,
                user.createdDate,
                ExpressionUtils.`as`(
                    JPAExpressions.select(userLog.loginDateTime.max())
                        .from(userLog)
                        .where(userLog.user.userNo.eq(userNo))
                        .orderBy(
                            userLog.loginDateTime.desc()
                        ), "lastLoginDateTime"
                )
            )
        )
            .from(user)
            .where(user.userNo.eq(userNo))
            .fetchOne()
    }


    override fun findExistsUserNickName(nickName: String): Boolean {
        return jpaQueryFactory.selectOne()
            .from(user)
            .where(user.nickName.eq(nickName)).fetchOne() == null
    }

    override fun findExistsUserId(userId: String): Boolean {
        return jpaQueryFactory.selectOne()
            .from(user)
            .where(user.userId.eq(userId)).fetchOne() == null
    }

}