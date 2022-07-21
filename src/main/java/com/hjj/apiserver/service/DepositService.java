package com.hjj.apiserver.service;

import com.hjj.apiserver.dto.DepositDto;
import com.hjj.apiserver.repositroy.DepositRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepositService {

    private final DepositRepository depositRepository;

    public List<DepositDto.ResponseDepositFindAll> findDepositListByBankType(){

        return depositRepository.findDepositAllByBankType();
    }
}
