package com.hjj.apiserver.repositroy.impl;

import com.hjj.apiserver.domain.QUserEntity;
import com.hjj.apiserver.domain.QUserLogEntity;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.QUserDto_ResponseUserDetails;
import com.hjj.apiserver.dto.UserDto;
import com.hjj.apiserver.repositroy.UserRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.hjj.apiserver.domain.QUserEntity.*;
import static com.hjj.apiserver.domain.QUserLogEntity.*;

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
