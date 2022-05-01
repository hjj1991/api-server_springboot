package com.hjj.apiserver.repositroy.impl;

import com.hjj.apiserver.domain.PurchaseEntity;
import com.hjj.apiserver.dto.PurchaseDto;
import com.hjj.apiserver.dto.QPurchaseDto_ResponsePurchaseDetail;
import com.hjj.apiserver.repositroy.PurchaseRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.hjj.apiserver.domain.QCardEntity.cardEntity;
import static com.hjj.apiserver.domain.QCategoryEntity.categoryEntity;
import static com.hjj.apiserver.domain.QPurchaseEntity.purchaseEntity;


@RequiredArgsConstructor
public class PurchaseRepositoryImpl implements PurchaseRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public PurchaseDto.ResponsePurchaseDetail findPurchase(Long userNo, Long purchaseNo){
        return jpaQueryFactory
                .select(new QPurchaseDto_ResponsePurchaseDetail(
                        purchaseEntity.accountBookEntity.accountBookNo,
                        purchaseEntity.cardEntity.cardNo,
                        categoryEntity.parentCategory.categoryNo,
                        purchaseEntity.categoryEntity.categoryNo,
                        purchaseEntity.storeName,
                        purchaseEntity.purchaseType,
                        purchaseEntity.price,
                        purchaseEntity.reason,
                        purchaseEntity.purchaseDate
                ))
                .from(purchaseEntity)
                .leftJoin(purchaseEntity.categoryEntity, categoryEntity)
                .where(purchaseEntity.purchaseNo.eq(purchaseNo).and(purchaseEntity.userEntity.userNo.eq(userNo)))
                .fetchOne();


    }

    @Override
    public List<PurchaseEntity> findPurchasePageCustom(PurchaseDto purchaseDto, Pageable pageable) {
        return jpaQueryFactory
                .select(purchaseEntity)
                .from(purchaseEntity)
                .leftJoin(purchaseEntity.cardEntity, cardEntity).fetchJoin()
                .leftJoin(purchaseEntity.categoryEntity, categoryEntity).fetchJoin()
                .where(
                        purchaseEntity.purchaseDate.between(purchaseDto.getStartDate(), purchaseDto.getEndDate())
                                .and(purchaseEntity.accountBookEntity.accountBookNo.eq(purchaseDto.getAccountBookNo())
                                        .and(purchaseEntity.deleteYn.eq('N'))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(purchaseEntity.purchaseDate.desc())
                .fetch();
    }



}
