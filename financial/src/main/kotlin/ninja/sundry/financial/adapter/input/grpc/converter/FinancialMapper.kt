package ninja.sundry.financial.adapter.input.grpc.converter

import domain.financial.FinancialCompany
import domain.financial.FinancialProduct
import domain.financial.FinancialProductOption

object FinancialMapper {

    fun FinancialProduct.toGrpc(): ninja.sundry.core.grpc.FinancialProduct {
        return ninja.sundry.core.grpc.FinancialProduct.newBuilder()
            .setFinancialProductId(this.financialProductId.toInt())
            .setFinancialProductName(this.financialProductName)
            .setJoinWay(this.joinWay)
            .setPostMaturityInterestRate(this.postMaturityInterestRate)
            .setSpecialCondition(this.specialCondition ?: "")
            .setJoinRestriction(this.joinRestriction.name)  // Enum을 문자열로 변환
            .setFinancialProductType(this.financialProductType.name)
            .setJoinMember(this.joinMember)
            .setAdditionalNotes(this.additionalNotes)
            .setMaxLimit(this.maxLimit?.toString() ?: "")
            .setDclsMonth(this.dclsMonth ?: "")
            .setDclsStartDay(this.dclsStartDay ?: "")
            .setDclsEndDay(this.dclsEndDay ?: "")
            .setFinancialCompany(this.financialCompany?.toGrpc() ?: ninja.sundry.core.grpc.FinancialCompany.getDefaultInstance())
            .addAllFinancialProductOptions(this.financialProductOptions.map { it.toGrpc() })
            .build()
    }

    fun FinancialCompany.toGrpc(): ninja.sundry.core.grpc.FinancialCompany {
        return ninja.sundry.core.grpc.FinancialCompany.newBuilder()
            .setDclsMonth(this.dclsMonth)
            .setCompanyName(this.companyName)
            .setDclsChrgMan(this.dclsChrgMan)
            .setHompUrl(this.hompUrl)
            .setCalTel(this.calTel)
            .setFinancialGroupType(this.financialGroupType.name)
            .build()
    }

    fun FinancialProductOption.toGrpc(): ninja.sundry.core.grpc.FinancialProductOption {
        return ninja.sundry.core.grpc.FinancialProductOption.newBuilder()
            .setInterestRateType(this.interestRateType.name)
            .setReserveType(this.reserveType?.name ?: "")
            .setDepositPeriodMonths(this.depositPeriodMonths)
            .setBaseInterestRate(this.baseInterestRate?.toFloat() ?: 0f)
            .setMaximumInterestRate(this.maximumInterestRate?.toFloat() ?: 0f)
            .build()
    }
}
