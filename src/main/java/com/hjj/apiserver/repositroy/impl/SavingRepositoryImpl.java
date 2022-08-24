package com.hjj.apiserver.repositroy.impl;

import com.hjj.apiserver.dto.SavingDto;
import com.hjj.apiserver.repositroy.SavingRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SavingRepositoryImpl implements SavingRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final ModelMapper modelMapper;

    @Override
    public List<SavingDto.ResponseSavingFindAll> findSavingAll() {
//        List<Saving> savings = jpaQueryFactory.select(saving)
//                .distinct()
//                .from(saving)
//                .join(saving.bank, bank).fetchJoin()
//                .leftJoin(saving.savingOptions, savingOption).fetchJoin()
//                .where(saving.enable.eq(1))
//                .fetch();
//
//
//        return savings.stream().map(tempSaving -> {
//            SavingDto.ResponseSavingFindAll responseSavingFindAll = modelMapper.map(tempSaving, SavingDto.ResponseSavingFindAll.class);
//            responseSavingFindAll.setFinCoNo(tempSaving.getBank().getFinCoNo());
//            responseSavingFindAll.setBankType(tempSaving.getBank().getBankType().getTitle());
//            responseSavingFindAll.setCalTel(tempSaving.getBank().getCalTel());
//            responseSavingFindAll.setDclsChrgMan(tempSaving.getBank().getDclsChrgMan());
//            responseSavingFindAll.setHompUrl(tempSaving.getBank().getHompUrl());
//            responseSavingFindAll.setKorCoNm(tempSaving.getBank().getKorCoNm());
//            responseSavingFindAll.setOptions(tempSaving.getSavingOptions().stream().map(option ->
//                    modelMapper.map(option, SavingDto.ResponseSavingFindAll.Option.class)).collect(Collectors.toList()));
//
//            return responseSavingFindAll;
//        }).collect(Collectors.toList());
        return new ArrayList<>();

    }

    @Override
    public List<SavingDto.SavingIntrRateDesc> findSavingByHome(){
//        QSavingOption subSaving = new QSavingOption("subSaving");
//
//        return jpaQueryFactory
//                .select(new QSavingDto_SavingIntrRateDesc(saving.korCoNm, saving.finPrdtNm, savingOption.intrRate, savingOption.intrRate2))
//                .from(saving)
//                .join(saving.bank, bank)
//                .join(saving.savingOptions, savingOption)
//                .on(saving.bank.eq(savingOption.saving.bank)
//                        .and(saving.finPrdtCd.eq(savingOption.saving.finPrdtCd))
//                        .and(savingOption.intrRate2.eq(
//                                JPAExpressions.select(subSaving.intrRate2.max())
//                                        .from(subSaving)
//                                        .where(subSaving.saving.bank.eq(saving.bank)
//                                                .and(subSaving.saving.finPrdtCd.eq(saving.finPrdtCd))
//                                                .and(subSaving.saveTrm.eq("12")))
//                                        .orderBy(subSaving.intrRate2.desc())
//                        ))
//                )
//                .where(saving.enable.eq(1))
//                .groupBy(saving.korCoNm, saving.finPrdtNm, savingOption.intrRate, savingOption.intrRate2)
//                .orderBy(savingOption.intrRate2.desc())
//                .limit(10)
//                .fetch();
        return new ArrayList<>();
    }
}
