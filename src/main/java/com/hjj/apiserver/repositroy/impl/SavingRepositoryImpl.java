package com.hjj.apiserver.repositroy.impl;

import com.hjj.apiserver.domain.Saving;
import com.hjj.apiserver.dto.QSavingDto_SavingIntrRateDesc;
import com.hjj.apiserver.dto.SavingDto;
import com.hjj.apiserver.repositroy.SavingRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

import static com.hjj.apiserver.domain.QBank.bank;
import static com.hjj.apiserver.domain.QSaving.saving;
import static com.hjj.apiserver.domain.QSavingOption.savingOption;

@RequiredArgsConstructor
public class SavingRepositoryImpl implements SavingRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final ModelMapper modelMapper;

    @Override
    public List<SavingDto.ResponseSavingFindAll> findSavingAll() {
        List<Saving> savings = jpaQueryFactory.select(saving)
                .distinct()
                .from(saving)
                .join(saving.bank, bank).fetchJoin()
                .leftJoin(saving.savingOptions, savingOption).fetchJoin()
                .where(saving.enable.eq(1))
                .fetch();


        return savings.stream().map(tempSaving -> {
            SavingDto.ResponseSavingFindAll responseSavingFindAll = modelMapper.map(tempSaving, SavingDto.ResponseSavingFindAll.class);
            responseSavingFindAll.setFinCoNo(tempSaving.getBank().getFinCoNo());
            responseSavingFindAll.setBankType(tempSaving.getBank().getBankType().getTitle());
            responseSavingFindAll.setCalTel(tempSaving.getBank().getCalTel());
            responseSavingFindAll.setDclsChrgMan(tempSaving.getBank().getDclsChrgMan());
            responseSavingFindAll.setHompUrl(tempSaving.getBank().getHompUrl());
            responseSavingFindAll.setKorCoNm(tempSaving.getBank().getKorCoNm());
            responseSavingFindAll.setOptions(tempSaving.getSavingOptions().stream().map(option ->
                    modelMapper.map(option, SavingDto.ResponseSavingFindAll.Option.class)).collect(Collectors.toList()));

            return responseSavingFindAll;
        }).collect(Collectors.toList());

    }

    @Override
    public List<SavingDto.SavingIntrRateDesc> findSavingByHome(){
        return jpaQueryFactory
                .select(new QSavingDto_SavingIntrRateDesc(saving.korCoNm, saving.finPrdtNm, savingOption.intrRate, savingOption.intrRate2))
                .from(saving)
                .join(saving.bank, bank)
                .leftJoin(saving.savingOptions, savingOption)
                .where(saving.enable.eq(1).and(savingOption.saveTrm.eq("12")))
                .groupBy(saving.finPrdtCd)
                .orderBy(savingOption.intrRate2.desc())
                .limit(10)
                .fetch();
    }
}
