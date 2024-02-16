package com.hjj.apiserver.repository.deposit

import com.hjj.apiserver.domain.bank.QBank.Companion.bank
import com.hjj.apiserver.domain.deposit.QDeposit.Companion.deposit
import com.hjj.apiserver.domain.deposit.QDepositOption.Companion.depositOption
import com.hjj.apiserver.dto.deposit.response.DepositFindAllResponse
import com.hjj.apiserver.dto.deposit.response.DepositIntrRateDescLimit10
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.modelmapper.ModelMapper

class DepositRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val modelMapper: ModelMapper,
) : DepositRepositoryCustom {
    override fun findDepositAll(): List<DepositFindAllResponse> {
        val deposits =
            jpaQueryFactory
                .select(deposit)
                .distinct()
                .from(deposit)
                .join(deposit.bank, bank).fetchJoin()
                .leftJoin(deposit.depositOptions, depositOption).fetchJoin()
                .where(deposit.enable.eq(1))
                .fetch()

        return deposits.map {
            val depositFindAllResponse = modelMapper.map(it, DepositFindAllResponse::class.java)
            depositFindAllResponse.finCoNo = it.bank!!.finCoNo
            depositFindAllResponse.bankType = it.bank!!.bankType.title
            depositFindAllResponse.calTel = it.bank!!.calTel
            depositFindAllResponse.dclsChrgMan = it.bank!!.dclsChrgMan
            depositFindAllResponse.hompUrl = it.bank!!.hompUrl
            depositFindAllResponse.korCoNm = it.bank!!.korCoNm
            depositFindAllResponse.options =
                it.depositOptions.map {
                        option ->
                    modelMapper.map(option, DepositFindAllResponse.Option::class.java)
                }
            depositFindAllResponse
        }
    }

    override fun findDepositByHome(): List<DepositIntrRateDescLimit10> {
        return jpaQueryFactory
            .select(
                Projections.constructor(
                    DepositIntrRateDescLimit10::class.java,
                    deposit.korCoNm,
                    deposit.finPrdtNm,
                    depositOption.intrRate,
                    depositOption.intrRate2,
                ),
            )
            .from(deposit)
            .join(deposit.bank, bank)
            .leftJoin(deposit.depositOptions, depositOption)
            .where(deposit.enable.eq(1).and(depositOption.saveTrm.eq("12")))
            .groupBy(deposit.korCoNm, deposit.finPrdtCd, deposit.finPrdtNm, depositOption.intrRate, depositOption.intrRate2)
            .orderBy(depositOption.intrRate2.desc())
            .limit(10)
            .fetch()
    }
}
