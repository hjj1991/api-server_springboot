package com.hjj.apiserver.repositroy;

import com.hjj.apiserver.dto.PurchaseDto;

public interface PurchaseRepositoryCustom {

    PurchaseDto.ResponsePurchaseDetail findPurchase(Long userNo, Long purchaseNo);
}
