package com.hjj.apiserver.repository.user

import com.hjj.apiserver.domain.user.QUser.user
import com.hjj.apiserver.domain.user.User
import com.querydsl.jpa.impl.JPAQueryFactory

class UserRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
): UserRepositoryCustom {
    override fun findUserLeftJoinUserLogByUserNo(userNo: Long): User? {
        TODO("Not yet implemented")
    }


    override fun findExistsUserNickName(nickName: String): Boolean {
        return jpaQueryFactory.selectOne()
            .from(user)
            .where(user.nickName.eq(nickName)).fetchOne() == null


    }
}