package com.hjj.apiserver.service;

import com.hjj.apiserver.common.exception.UserNotFoundException;
import com.hjj.apiserver.domain.*;
import com.hjj.apiserver.dto.CardDto;
import com.hjj.apiserver.dto.CategoryDto;
import com.hjj.apiserver.dto.PurchaseDto;
import com.hjj.apiserver.repositroy.*;
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

    private final AccountBookRepository accountBookRepository;
    private final CategoryRepository categoryRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;


    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void addPurchase(PurchaseDto purchaseDto) throws Exception {
        UserEntity userEntity = userRepository.getById(purchaseDto.getUserNo());
        if(userEntity != null){
            purchaseDto.setUserEntity(userEntity);
        }

        AccountBookEntity accountBookEntity = accountBookRepository.findAccountBookBySubQuery(purchaseDto.getUserNo(), purchaseDto.getAccountBookNo()).orElseThrow(UserNotFoundException::new);
        purchaseDto.setAccountBookEntity(accountBookEntity);

        if(purchaseDto.getCardNo() != null){
            CardEntity cardEntity = cardRepository.getById(purchaseDto.getCardNo());
            if(cardEntity == null)
                throw new Exception("해당 되는 카드가 존재하지 않습니다.");
            purchaseDto.setCardEntity(cardEntity);
        }
        if(purchaseDto.getCategoryNo() != null){
            List<AccountBookUserEntity.AccountRole> accountRoleList = new ArrayList<>();
            accountRoleList.add(AccountBookUserEntity.AccountRole.OWNER);
            accountRoleList.add(AccountBookUserEntity.AccountRole.MEMBER);
            CategoryEntity categoryEntity = categoryRepository.findByCategoryNoAndSubQuery(purchaseDto.getCategoryNo(), purchaseDto.getAccountBookNo(), purchaseDto.getUserNo(), accountRoleList).orElseThrow(() -> new Exception("해당하는 카테고리가 없습니다."));
            purchaseDto.setCategoryEntity(categoryEntity);
        }

        PurchaseEntity purchaseEntity = purchaseDto.toEntity();

        purchaseRepository.save(purchaseEntity);

    }

    public List<PurchaseDto.ResponsePurchaseList.Purchase> findPurchaseList(PurchaseDto purchaseDto){

        List<PurchaseEntity> purchaseEntityList = purchaseRepository.findAllEntityGraphByPurchaseDateBetweenAndAccountBookEntity_AccountBookNoAndDeleteYnOrderByPurchaseDateDesc(purchaseDto.getStartDate(), purchaseDto.getEndDate(), purchaseDto.getAccountBookNo(), 'N');
        List<PurchaseDto.ResponsePurchaseList.Purchase> purchaseList = new ArrayList<>();
        purchaseEntityList.stream().forEach(purchaseEntity -> {
            PurchaseDto.ResponsePurchaseList.Purchase responsePurchaseList = modelMapper.map(purchaseEntity, PurchaseDto.ResponsePurchaseList.Purchase.class);
            responsePurchaseList.setAccountBookNo(purchaseDto.getAccountBookNo());
            responsePurchaseList.setUserNo(purchaseDto.getUserNo());
            if(purchaseEntity.getCardEntity() != null){
                CardDto cardDto = modelMapper.map(purchaseEntity.getCardEntity(), CardDto.class);
                responsePurchaseList.setCardDto(cardDto);
            }
            if(purchaseEntity.getCategoryEntity() != null){
                CategoryDto.PurchaseCategoryInfo categoryInfo =  modelMapper.map(purchaseEntity.getCategoryEntity(), CategoryDto.PurchaseCategoryInfo.class);
                if(purchaseEntity.getCategoryEntity().getParentCategory() == null){
                    categoryInfo.setParentCategoryNo(purchaseEntity.getCategoryEntity().getCategoryNo());
                    categoryInfo.setParentCategoryName(purchaseEntity.getCategoryEntity().getCategoryName());
                }else{
                    categoryInfo.setParentCategoryNo(purchaseEntity.getCategoryEntity().getParentCategory().getCategoryNo());
                    categoryInfo.setParentCategoryName(purchaseEntity.getCategoryEntity().getParentCategory().getCategoryName());
                }

                responsePurchaseList.setCategoryInfo(categoryInfo);
            }
            purchaseList.add(responsePurchaseList);
        });
        return purchaseList;
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deletePurchase(Long userNo, Long purchaseNo) throws Exception{
        PurchaseEntity purchaseEntity = purchaseRepository.findEntityGraphByUserEntity_UserNoAndPurchaseNoAndDeleteYn(userNo, purchaseNo, 'N');
        if(purchaseEntity == null){
            throw new Exception("해당값이 존재하지 않습니다.");
        }
        purchaseEntity.delete();

    }

    public PurchaseDto.ResponsePurchaseDetail findPurchase(Long userNo, Long purchaseNo) {
        return purchaseRepository.findPurchase(userNo, purchaseNo);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void modifyPurchase(PurchaseDto purchaseDto) {
        PurchaseEntity updatePurchaseEntity = purchaseRepository.findEntityGraphByUserEntity_UserNoAndPurchaseNoAndDeleteYn(purchaseDto.getUserNo(), purchaseDto.getPurchaseNo(), 'N');

        updatePurchaseEntity.updatePurchase(purchaseDto);

    }
}




