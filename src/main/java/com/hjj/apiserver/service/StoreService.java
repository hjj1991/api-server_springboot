package com.hjj.apiserver.service;

import com.hjj.apiserver.domain.StoreEntity;
import com.hjj.apiserver.dto.StoreDto;
import com.hjj.apiserver.repositroy.StoreRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;
    private final ModelMapper modelMapper;

    public List<StoreDto> selectStoreList(){
        List<StoreEntity> storeEntityList = storeRepository.findAll();
        List<StoreDto> storeDtoList = new ArrayList<>();

        storeEntityList.stream().forEach(storeEntity -> {
            StoreDto storeDto = modelMapper.map(storeEntity, StoreDto.class);
            storeDtoList.add(storeDto);
        });

        return storeDtoList;
    }
}
