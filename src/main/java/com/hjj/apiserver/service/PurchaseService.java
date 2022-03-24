package com.hjj.apiserver.service;

import com.hjj.apiserver.domain.CardEntity;
import com.hjj.apiserver.domain.PurchaseEntity;
import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.CardDto;
import com.hjj.apiserver.dto.PurchaseDto;
import com.hjj.apiserver.dto.TokenDto;
import com.hjj.apiserver.repositroy.CardRepository;
import com.hjj.apiserver.repositroy.PurchaseRepository;
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
//        if(purchaseDto.getStoreNo() != null){
//            CategoryEntity categoryEntity = storeRepository.getById(purchaseDto.getStoreNo());
//            if(categoryEntity == null)
//                throw new Exception("해당 하는 업종이 존재하지 않습니다.");
//            purchaseDto.setStoreInfo(categoryEntity);
//        }

        PurchaseEntity purchaseEntity = purchaseDto.toEntity();

        purchaseRepository.save(purchaseEntity);

    }

    public List<PurchaseDto.ResponseGetPurchase> findPurchaseList(TokenDto user, PurchaseDto.RequestGetPurchaseListForm form){

        List<PurchaseEntity> purchaseEntityList = purchaseRepository.findAllEntityGraphByPurchaseDateBetweenAndUserInfo_UserNoAndDeleteYnOrderByPurchaseDateDesc(form.getStartDate(), form.getEndDate(), user.getUserNo(), 'N');
        List<PurchaseDto.ResponseGetPurchase> purchaseList = new ArrayList<>();
        purchaseEntityList.stream().forEach(purchaseEntity -> {
            PurchaseDto.ResponseGetPurchase responseGetPurchase = modelMapper.map(purchaseEntity, PurchaseDto.ResponseGetPurchase.class);
            if(purchaseEntity.getCardInfo() != null){
                CardDto cardDto = modelMapper.map(purchaseEntity.getCardInfo(), CardDto.class);
                responseGetPurchase.setCardInfo(cardDto);
            }
            purchaseList.add(responseGetPurchase);
        });
        return purchaseList;
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deletePurchase(Long userNo, Long purchaseNo) throws Exception{
        PurchaseEntity purchaseEntity = purchaseRepository.findEntityGraphByUserInfo_UserNoAndPurchaseNoAndDeleteYn(userNo, purchaseNo, 'N');
        if(purchaseEntity == null){
            throw new Exception("해당값이 존재하지 않습니다.");
        }
        purchaseEntity.delete();

    }
}




