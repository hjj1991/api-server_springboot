package com.hjj.apiserver.service;

import com.hjj.apiserver.domain.PurchaseEntity;
import com.hjj.apiserver.dto.PurchaseDto;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PurchaseServiceTest {

    @Autowired private PurchaseService purchaseService;
    @Autowired private ModelMapper modelMapper;


    @Test
    public void 소비추가() throws Exception {

        PurchaseDto.RequestAddPurchaseForm requestAddPurchaseForm = new PurchaseDto.RequestAddPurchaseForm();
        requestAddPurchaseForm.setStoreNo(1L);
        requestAddPurchaseForm.setPrice(1000);
        requestAddPurchaseForm.setPurchaseDate(LocalDate.now());
        requestAddPurchaseForm.setPurchaseType(PurchaseEntity.PurchaseType.OUTGOING);
        requestAddPurchaseForm.setReason("테스트용도로 등록해봅니다.");
        requestAddPurchaseForm.setCardNo(1L);

        PurchaseDto purchaseDto = modelMapper.map(requestAddPurchaseForm, PurchaseDto.class);
        purchaseDto.setUserNo(1L);

        purchaseService.addPurchase(purchaseDto);
    }

}