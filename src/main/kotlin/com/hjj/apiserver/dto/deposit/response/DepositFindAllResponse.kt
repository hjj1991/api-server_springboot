package com.hjj.apiserver.dto.deposit.response

import com.hjj.apiserver.domain.bank.BankType

class DepositFindAllResponse(
    var finCoSubmDay: String? = null,
    var dclsStrtDay: String? = null,
    var dclsEndDay: String? = null,
    var maxLimit: Long? = null,
    var etcNote: String? = null,
    var joinMember: String? = null,
    var joinDeny: String? = null,
    var spclCnd: String? = null,
    var mtrtInt: String? = null,
    var joinWay: String? = null,
    var finPrdtNm: String? = null,
    var korCoNm: String? = null,
    var finPrdtCd: String? = null,
    var finCoNo: String? = null,
    var dclsMonth: String? = null,
    var bankType: BankType? = null,
    var calTel: String? = null,
    var hompUrl: String? = null,
    var dclsChrgMan: String? = null,
    var options: List<Option> = listOf(),
) {

    class Option(
        var intrRate2: Double? = null,
        var intrRate: Double? = null,
        var saveTrm: String? = null,
        var intrRateTypeNm: String? = null,
        var intrRateType: String? = null,
    ) {

    }
}