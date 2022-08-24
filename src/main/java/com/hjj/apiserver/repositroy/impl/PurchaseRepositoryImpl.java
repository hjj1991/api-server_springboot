package com.hjj.apiserver.repositroy.impl;

import com.hjj.apiserver.domain.purchase.Purchase;
import com.hjj.apiserver.dto.PurchaseDto;
import com.hjj.apiserver.repositroy.PurchaseRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
public class PurchaseRepositoryImpl implements PurchaseRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public PurchaseDto.ResponsePurchaseDetail findPurchase(Long userNo, Long purchaseNo){
//        return jpaQueryFactory
//                .select(new QPurchaseDto_ResponsePurchaseDetail(
//                        purchaseEntity.accountBookEntity.accountBookNo,
//                        purchaseEntity.cardEntity.cardNo,
//                        categoryEntity.parentCategory.categoryNo,
//                        purchaseEntity.categoryEntity.categoryNo,
//                        purchaseEntity.storeName,
//                        purchaseEntity.purchaseType,
//                        purchaseEntity.price,
//                        purchaseEntity.reason,
//                        purchaseEntity.purchaseDate
//                ))
//                .from(purchaseEntity)
//                .leftJoin(purchaseEntity.categoryEntity, categoryEntity)
//                .where(purchaseEntity.purchaseNo.eq(purchaseNo).and(purchaseEntity.userEntity.userNo.eq(userNo)))
//                .fetchOne();

        return null;

    }

    @Override
    public List<Purchase> findPurchasePageCustom(PurchaseDto purchaseDto, Pageable pageable) {
//        return jpaQueryFactory
//                .select(purchaseEntity)
//                .from(purchaseEntity)
//                .leftJoin(purchaseEntity.cardEntity, cardEntity).fetchJoin()
//                .leftJoin(purchaseEntity.categoryEntity, categoryEntity).fetchJoin()
//                .where(
//                        purchaseEntity.purchaseDate.between(purchaseDto.getStartDate(), purchaseDto.getEndDate())
//                                .and(purchaseEntity.accountBookEntity.accountBookNo.eq(purchaseDto.getAccountBookNo())
//                                        .and(purchaseEntity.deleteYn.eq('N'))))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize() + 1)
//                .orderBy(purchaseEntity.purchaseDate.desc(), purchaseEntity.purchaseNo.desc())
//                .fetch();
        return new ArrayList<>();
    }



}
