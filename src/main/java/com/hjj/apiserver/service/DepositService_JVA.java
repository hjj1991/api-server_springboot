package com.hjj.apiserver.service;

import com.hjj.apiserver.dto.DepositDto;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class DepositService_JVA {

    private final DepositRepository depositRepository;

    public List<DepositDto.ResponseDepositFindAll> findDepositList(){

        return depositRepository.findDepositAll();
    }
}
