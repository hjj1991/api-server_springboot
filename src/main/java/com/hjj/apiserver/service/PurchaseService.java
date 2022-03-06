package com.hjj.apiserver.service;

import com.hjj.apiserver.domain.CardEntity;
import com.hjj.apiserver.domain.PurchaseEntity;
import com.hjj.apiserver.domain.StoreEntity;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.CardDto;
import com.hjj.apiserver.dto.PurchaseDto;
import com.hjj.apiserver.dto.StoreDto;
import com.hjj.apiserver.repositroy.CardRepository;
import com.hjj.apiserver.repositroy.PurchaseRepository;
import com.hjj.apiserver.repositroy.StoreRepository;
import com.hjj.apiserver.repositroy.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final StoreRepository storeRepository;
    private final ModelMapper modelMapper;


    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void addPurchase(PurchaseDto purchaseDto) throws Exception {
        UserEntity userEntity = userRepository.getById(purchaseDto.getUserNo());
        if(userEntity != null){
            purchaseDto.setUserInfo(userEntity);
        }

        if(purchaseDto.getCardNo() != null){
            CardEntity cardEntity = cardRepository.getById(purchaseDto.getCardNo());
            if(cardEntity == null)
                throw new Exception("해당 되는 카드가 존재하지 않습니다.");
            purchaseDto.setCardInfo(cardEntity);
        }
        if(purchaseDto.getStoreNo() != null){
            StoreEntity storeEntity = storeRepository.getById(purchaseDto.getStoreNo());
            if(storeEntity == null)
                throw new Exception("해당 하는 업종이 존재하지 않습니다.");
            purchaseDto.setStoreInfo(storeEntity);
        }





        PurchaseEntity purchaseEntity = purchaseDto.toEntity();
        purchaseRepository.save(purchaseEntity);

    }

    public List<PurchaseDto.ResponseGetPurchase> getPurchaseList(PurchaseDto.RequestGetPurchaseListForm form){

        List<PurchaseEntity> purchaseEntityList = purchaseRepository.findAllFetchJoinByStartDateAndEndDate(form.getStartDate(), form.getEndDate());
        List<PurchaseDto.ResponseGetPurchase> purchaseList = new ArrayList<>();
        purchaseEntityList.stream().forEach(purchaseEntity -> {
            PurchaseDto.ResponseGetPurchase responseGetPurchase = modelMapper.map(purchaseEntity, PurchaseDto.ResponseGetPurchase.class);
            if(purchaseEntity.getCardInfo() != null){
                CardDto cardDto = modelMapper.map(purchaseEntity.getCardInfo(), CardDto.class);
                responseGetPurchase.setCardInfo(cardDto);
            }

            if(purchaseEntity.getStoreInfo() != null){
                StoreDto storeDto = modelMapper.map(purchaseEntity.getStoreInfo(), StoreDto.class);
                responseGetPurchase.setStoreInfo(storeDto);
            }



            purchaseList.add(responseGetPurchase);
        });
        return purchaseList;
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deletePurchase(Long userNo, Long purchaseNo) throws Exception{
        PurchaseEntity purchaseEntity = purchaseRepository.findByFechJoinUserNoAndPurchaseNoAndDeleteYn(userNo, purchaseNo);
        if(purchaseEntity == null){
            throw new Exception("해당값이 존재하지 않습니다.");
        }
        purchaseEntity.delete();

    }
}




