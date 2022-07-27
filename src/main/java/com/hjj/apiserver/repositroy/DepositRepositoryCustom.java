package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.dto.DepositDto;
import com.querydsl.core.Tuple;

import java.util.List;

public interface DepositRepositoryCustom {

    List<DepositDto.ResponseDepositFindAll> findDepositAll();
    List<DepositDto.DepositIntrRateDesc>  findDepositByHome();
}
