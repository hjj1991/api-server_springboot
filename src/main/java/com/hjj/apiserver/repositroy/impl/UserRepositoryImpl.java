package com.hjj.apiserver.repositroy.impl;

import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.repositroy.UserRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.hjj.apiserver.domain.QUserEntity.userEntity;
import static com.hjj.apiserver.domain.QUserLogEntity.userLogEntity;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public UserEntity findUserLeftJoinUserLogByUserNo(Long userNo) {

        return jpaQueryFactory
                .select(userEntity)
                .from(userEntity)
                .leftJoin(userEntity.userLogEntityList, userLogEntity).fetchJoin()
                .where(userEntity.userNo.eq(userNo))
                .fetchOne();
    }
}
