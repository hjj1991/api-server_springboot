package com.hjj.apiserver.repositroy.impl;

import com.hjj.apiserver.dto.PurchaseDto;
import com.hjj.apiserver.dto.QPurchaseDto_ResponsePurchaseDetail;
import com.hjj.apiserver.repositroy.PurchaseRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.hjj.apiserver.domain.QPurchaseEntity.purchaseEntity;


@RequiredArgsConstructor
public class PurchaseRepositoryImpl implements PurchaseRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public PurchaseDto.ResponsePurchaseDetail findPurchase(Long userNo, Long purchaseNo){
        return jpaQueryFactory
                .select(new QPurchaseDto_ResponsePurchaseDetail(
                        purchaseEntity.accountBookEntity.accountBookNo,
                        purchaseEntity.cardEntity.cardNo,
                        purchaseEntity.categoryEntity.categoryNo,
                        purchaseEntity.storeName,
                        purchaseEntity.purchaseType,
                        purchaseEntity.price,
                        purchaseEntity.reason,
                        purchaseEntity.purchaseDate
                ))
                .from(purchaseEntity)
                .where(purchaseEntity.purchaseNo.eq(purchaseNo).and(purchaseEntity.userEntity.userNo.eq(userNo)))
                .fetchOne();


    }
}
