package com.hjj.apiserver.service;

import com.hjj.apiserver.dto.DepositDto;
import com.hjj.apiserver.dto.SavingDto;
import com.hjj.apiserver.repositroy.SavingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingService {

    private final SavingRepository savingRepository;


    public List<SavingDto.ResponseSavingFindAll> findSavingList(){

        return savingRepository.findSavingAll();
    }
}
