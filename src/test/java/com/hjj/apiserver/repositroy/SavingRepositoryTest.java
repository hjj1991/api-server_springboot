package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.dto.SavingDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SavingRepositoryTest {

    @Autowired SavingRepository savingRepository;

    @Test
    void getDepositOrderByIntrRate2DescLimit10(){

        List<SavingDto.SavingIntrRateDesc> savingByHome = savingRepository.findSavingByHome();

        System.out.println(savingByHome);

    }

}