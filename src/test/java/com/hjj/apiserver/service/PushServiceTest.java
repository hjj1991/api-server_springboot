package com.hjj.apiserver.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PushServiceTest {

    @Autowired PushService pushService;

    @Test
    void pushLineNotiTest() throws Exception {
        pushService.pushLineNoti("리브메이트 2022-06-15\n" +
                "정답:ㅎㅎㅎ");
    }

}