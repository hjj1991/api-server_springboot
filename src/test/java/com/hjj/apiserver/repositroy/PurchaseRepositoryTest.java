package com.hjj.apiserver.repositroy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class PurchaseRepositoryTest {
    @Autowired PurchaseRepository purchaseRepository;
    @Autowired ObjectMapper objectMapper;

    @Test
    void purchasePagingTest() throws JsonProcessingException {
        PageRequest pageRequest = PageRequest.of(0, 3);

    }

}