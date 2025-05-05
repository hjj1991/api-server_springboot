package com.hjj.apiserver.adapter.out.persistence.user.repository

import com.hjj.apiserver.adapter.out.persistence.user.entity.QUserEntity.Companion.userEntity
import com.querydsl.jpa.impl.JPAQueryFactory

class UserRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : UserRepositoryCustom {
    override fun existsByUserNickName(nickName: String): Boolean {
        return jpaQueryFactory.selectOne()
            .from(userEntity)
            .where(userEntity.nickName.eq(nickName)).fetchOne() != null
    }
}
