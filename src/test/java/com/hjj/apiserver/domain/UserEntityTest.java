package com.hjj.apiserver.domain;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static org.junit.jupiter.api.Assertions.*;


class UserEntityTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Test
    void builder_UserEntity() {
        UserEntity userEntity = UserEntity.builder()
                .userId("ㅎㅇㅎㅇ")
                .userEmail("재정이")
                .build();
        logger.info(userEntity.getUserId());
        logger.info("흠 {}", userEntity.getUserId());
        logger.info("항 {}", userEntity.getUserEmail());

    }

}