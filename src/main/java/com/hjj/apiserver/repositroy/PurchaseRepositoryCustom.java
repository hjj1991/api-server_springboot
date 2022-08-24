package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.domain.purchase.Purchase;
import com.hjj.apiserver.dto.PurchaseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PurchaseRepositoryCustom {

    PurchaseDto.ResponsePurchaseDetail findPurchase(Long userNo, Long purchaseNo);
    List<Purchase> findPurchasePageCustom(PurchaseDto purchaseDto, Pageable pageable);
}
