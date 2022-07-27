package com.hjj.apiserver.repositroy.impl;

import com.hjj.apiserver.domain.Deposit;
import com.hjj.apiserver.dto.DepositDto;
import com.hjj.apiserver.dto.QDepositDto_DepositIntrRateDesc;
import com.hjj.apiserver.repositroy.DepositRepositoryCustom;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

import static com.hjj.apiserver.domain.QBank.bank;
import static com.hjj.apiserver.domain.QDeposit.deposit;
import static com.hjj.apiserver.domain.QDepositOption.depositOption;

@RequiredArgsConstructor
public class DepositRepositoryImpl implements DepositRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final ModelMapper modelMapper;

    public List<DepositDto.ResponseDepositFindAll> findDepositAll(){

        List<Deposit> deposits = jpaQueryFactory
                .select(deposit)
                .distinct()
                .from(deposit)
                .join(deposit.bank, bank).fetchJoin()
                .leftJoin(deposit.depositOptions, depositOption).fetchJoin()
                .where(deposit.enable.eq(1))
                .fetch();

        return deposits.stream().map(tempDeposit ->{
            DepositDto.ResponseDepositFindAll responseDepositFindAll = modelMapper.map(tempDeposit, DepositDto.ResponseDepositFindAll.class);
            responseDepositFindAll.setFinCoNo(tempDeposit.getBank().getFinCoNo());
            responseDepositFindAll.setBankType(tempDeposit.getBank().getBankType().getTitle());
            responseDepositFindAll.setCalTel(tempDeposit.getBank().getCalTel());
            responseDepositFindAll.setDclsChrgMan(tempDeposit.getBank().getDclsChrgMan());
            responseDepositFindAll.setHompUrl(tempDeposit.getBank().getHompUrl());
            responseDepositFindAll.setKorCoNm(tempDeposit.getBank().getKorCoNm());
            responseDepositFindAll.setOptions(tempDeposit.getDepositOptions().stream().map(option ->
                    modelMapper.map(option, DepositDto.ResponseDepositFindAll.Option.class)).collect(Collectors.toList()));
            return responseDepositFindAll;
        }).collect(Collectors.toList());
    }

    public List<DepositDto.DepositIntrRateDesc> findDepositByHome(){

        return jpaQueryFactory
                .select(new QDepositDto_DepositIntrRateDesc(
                        deposit.korCoNm, deposit.finPrdtNm, depositOption.intrRate2, depositOption.intrRate))
                .from(deposit)
                .join(deposit.bank, bank)
                .leftJoin(deposit.depositOptions, depositOption)
                .where(deposit.enable.eq(1).and(depositOption.saveTrm.eq("12")))
                .groupBy(deposit.korCoNm, deposit.finPrdtCd, deposit.finPrdtNm, depositOption.intrRate, depositOption.intrRate2)
                .orderBy(depositOption.intrRate2.desc())
                .limit(10)
                .fetch();
    }
}
