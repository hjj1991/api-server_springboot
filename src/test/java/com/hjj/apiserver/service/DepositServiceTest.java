package com.hjj.apiserver.service;

import com.hjj.apiserver.domain.Bank;
import com.hjj.apiserver.dto.DepositDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DepositServiceTest {

    @Autowired DepositService depositService;

    @Test
    void findDepositListByBankType(){

        List<DepositDto> depositDtos = depositService.findDepositListByBankType(Bank.BankType.BANK);

    }


}