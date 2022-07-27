package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.dto.DepositDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DepositRepositoryTest {

    @Autowired DepositRepository depositRepository;

    @Test
    void getDepositOrderByIntrRate2DescLimit10(){

        List<DepositDto.DepositIntrRateDesc> intrRateDescList = depositRepository.findDepositByHome();

    }

}