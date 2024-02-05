package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.adapter.out.persistence.user.QUserEntity.Companion.userEntity
import com.querydsl.jpa.impl.JPAQueryFactory

class UserRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : UserRepositoryCustom {


    override fun findExistsUserNickName(nickName: String): Boolean {
        return jpaQueryFactory.selectOne()
            .from(userEntity)
            .where(userEntity.nickName.eq(nickName)).fetchOne() != null
    }
}