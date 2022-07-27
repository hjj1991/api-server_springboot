package com.hjj.apiserver.service;

import com.hjj.apiserver.dto.MainDto;
import com.hjj.apiserver.repositroy.DepositRepository;
import com.hjj.apiserver.repositroy.SavingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainService {

    private final DepositRepository depositRepository;
    private final SavingRepository savingRepository;


    public MainDto.ResponseMain findMain(){

        return MainDto.ResponseMain.builder()
                .deposits(depositRepository.findDepositByHome())
                .savings(savingRepository.findSavingByHome())
                .build();
    }
}
