package com.hjj.apiserver.repository.saving

import com.hjj.apiserver.domain.bank.QBank.*
import com.hjj.apiserver.domain.saving.QSaving.*
import com.hjj.apiserver.domain.saving.QSavingOption
import com.hjj.apiserver.domain.saving.QSavingOption.*
import com.hjj.apiserver.dto.saving.response.SavingFindAllResponse
import com.hjj.apiserver.dto.saving.response.SavingIntrRateDescLimit10
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.modelmapper.ModelMapper

class SavingRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val modelMapper: ModelMapper,
): SavingRepositoryCustom {
    override fun findSavingAll(): List<SavingFindAllResponse> {
        val savings = jpaQueryFactory.select(saving)
            .distinct()
            .from(saving)
            .join(saving.bank, bank).fetchJoin()
            .leftJoin(saving.savingOptions, savingOption).fetchJoin()
            .where(saving.enable.eq(1))
            .fetch()

        return savings.map {
            val savingFindAllResponse = modelMapper.map(it, SavingFindAllResponse::class.java)
            savingFindAllResponse.finCoNo = it.bank!!.finCoNo
            savingFindAllResponse.bankType = it.bank!!.bankType
            savingFindAllResponse.calTel = it.bank!!.calTel
            savingFindAllResponse.dclsChrgMan = it.bank!!.dclsChrgMan
            savingFindAllResponse.hompUrl = it.bank!!.hompUrl
            savingFindAllResponse.korCoNm = it.bank!!.korCoNm
            savingFindAllResponse.options = it.savingOptions.map { modelMapper.map(it, SavingFindAllResponse.Option::class.java) }

            savingFindAllResponse
        }
    }

    override fun findSavingByHome(): List<SavingIntrRateDescLimit10> {
        val subSaving = QSavingOption("subSaving")

        return jpaQueryFactory
            .select(Projections.constructor(
                SavingIntrRateDescLimit10::class.java,
                saving.korCoNm,
                saving.finPrdtNm,
                savingOption.intrRate,
                savingOption.intrRate2
            ))
            .from(saving)
            .join(saving.bank, bank)
            .join(saving.savingOptions, savingOption)
            .on(saving.bank.eq(savingOption.saving.bank)
                .and(saving.finPrdtCd.eq(savingOption.saving.finPrdtCd))
                .and(savingOption.intrRate2.eq(
                    JPAExpressions.select(subSaving.intrRate2.max())
                        .from(subSaving)
                        .where(subSaving.saving.bank.eq(saving.bank)
                            .and(subSaving.saving.finPrdtCd.eq(saving.finPrdtCd))
                            .and(subSaving.saveTrm.eq("12")))
                        .orderBy(subSaving.intrRate2.desc())
                ))
            )
            .where(saving.enable.eq(1))
            .groupBy(saving.korCoNm, saving.finPrdtNm, savingOption.intrRate, savingOption.intrRate2)
            .orderBy(savingOption.intrRate2.desc())
            .limit(10)
            .fetch()

    }
}