package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.dto.DepositDto;

import java.util.List;

public interface DepositRepositoryCustom {

    List<DepositDto.ResponseDepositFindAll> findDepositAll();
}
