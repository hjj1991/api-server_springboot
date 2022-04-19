package com.hjj.apiserver.repositroy.impl;

import com.hjj.apiserver.dto.PurchaseDto;
import com.hjj.apiserver.dto.QPurchaseDto_ResponsePurchaseDetail;
import com.hjj.apiserver.repositroy.PurchaseRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.hjj.apiserver.domain.QAccountBookEntity.accountBookEntity;
import static com.hjj.apiserver.domain.QCardEntity.cardEntity;
import static com.hjj.apiserver.domain.QCategoryEntity.categoryEntity;
import static com.hjj.apiserver.domain.QPurchaseEntity.purchaseEntity;
import static com.hjj.apiserver.domain.QUserEntity.userEntity;


@RequiredArgsConstructor
public class PurchaseRepositoryImpl implements PurchaseRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public PurchaseDto.ResponsePurchaseDetail findPurchase(Long userNo, Long purchaseNo){
        return jpaQueryFactory
                .select(new QPurchaseDto_ResponsePurchaseDetail(
                        purchaseEntity.accountBookInfo.accountBookNo,
                        purchaseEntity.cardInfo.cardNo,
                        purchaseEntity.categoryInfo.categoryNo,
                        purchaseEntity.storeName,
                        purchaseEntity.purchaseType,
                        purchaseEntity.price,
                        purchaseEntity.reason,
                        purchaseEntity.purchaseDate
                ))
                .from(purchaseEntity)
                .join(purchaseEntity.userInfo, userEntity)
                .leftJoin(purchaseEntity.accountBookInfo, accountBookEntity)
                .leftJoin(purchaseEntity.cardInfo, cardEntity)
                .leftJoin(purchaseEntity.categoryInfo, categoryEntity)
                .where(purchaseEntity.purchaseNo.eq(purchaseNo).and(purchaseEntity.userInfo.userNo.eq(userNo)))
                .fetchOne();


    }
}
