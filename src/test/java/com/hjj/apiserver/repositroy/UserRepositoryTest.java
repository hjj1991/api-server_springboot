package com.hjj.apiserver.repositroy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hjj.apiserver.domain.QUserEntity;
import com.hjj.apiserver.domain.QUserLogEntity;
import com.hjj.apiserver.domain.UserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.hjj.apiserver.domain.QUserEntity.*;
import static com.hjj.apiserver.domain.QUserLogEntity.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {
    @Autowired private JPAQueryFactory jpaQueryFactory;
    @Autowired private ObjectMapper objectMapper;


    @Test
    void findUserJoinUserLogByUserNo() throws JsonProcessingException {
        UserEntity user = jpaQueryFactory
                .select(userEntity)
                .from(userEntity)
                .leftJoin(userEntity.userLogEntityList, userLogEntity)
                .where(userEntity.userNo.eq(1L))
                .fetchOne();

        user.getUserLogEntityList().forEach(System.out::println);
    }

}