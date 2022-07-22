package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.dto.SavingDto;

import java.util.List;

public interface SavingRepositoryCustom {
    List<SavingDto.ResponseSavingFindAll> findSavingAll();
}
