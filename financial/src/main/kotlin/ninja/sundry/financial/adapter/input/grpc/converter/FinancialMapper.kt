package ninja.sundry.financial.adapter.input.grpc.converter

import com.google.protobuf.DoubleValue
import com.google.protobuf.Int64Value
import com.google.protobuf.StringValue
import domain.financial.FinancialCompany
import domain.financial.FinancialProduct
import domain.financial.FinancialProductOption
import ninja.sundry.core.grpc.FinancialCompanyGrpc
import ninja.sundry.core.grpc.FinancialProductGrpc
import ninja.sundry.core.grpc.FinancialProductOptionGrpc

object FinancialMapper {

    fun FinancialProduct.toFinancialProductGrpc(): FinancialProductGrpc {
        return FinancialProductGrpc.newBuilder()
            .setFinancialProductId(this.financialProductId)
            .setFinancialProductName(this.financialProductName)
            .setJoinWay(this.joinWay)
            .setPostMaturityInterestRate(StringValue.of(this.postMaturityInterestRate)?: StringValue.newBuilder().build())
            .setSpecialCondition(StringValue.of(this.specialCondition)?: StringValue.newBuilder().build())
            .setJoinRestriction(this.joinRestriction.name)  // Enum을 문자열로 변환
            .setFinancialProductType(this.financialProductType.name)
            .setJoinMember(this.joinMember)
            .setAdditionalNotes(this.additionalNotes)
            .setMaxLimit(this.maxLimit?.let { Int64Value.of(it) } ?: Int64Value.newBuilder().build())
            .setDclsMonth(this.dclsMonth?.let { StringValue.of(it) ?: StringValue.newBuilder().build()  })
            .setDclsStartDay(this.dclsStartDay?.let { StringValue.of(it) ?: StringValue.newBuilder().build() })
            .setDclsEndDay(this.dclsEndDay?.let { StringValue.of(it) } ?: StringValue.newBuilder().build())
            .setFinancialCompany(this.financialCompany?.toFinancialCompanyGrpc() ?: FinancialCompanyGrpc.getDefaultInstance())
            .addAllFinancialProductOptions(this.financialProductOptions.map { it.toFinancialProductOptionGrpc() })
            .build()
    }

    fun FinancialCompany.toFinancialCompanyGrpc(): FinancialCompanyGrpc {
        return FinancialCompanyGrpc.newBuilder()
            .setDclsMonth(this.dclsMonth)
            .setCompanyName(this.companyName)
            .setDclsChrgMan(StringValue.of(this.dclsChrgMan) ?: StringValue.newBuilder().build())
            .setHompUrl(StringValue.of(this.hompUrl) ?: StringValue.newBuilder().build())
            .setCalTel(StringValue.of(this.calTel) ?: StringValue.newBuilder().build())
            .setFinancialGroupType(this.financialGroupType.name)
            .build()
    }

    fun FinancialProductOption.toFinancialProductOptionGrpc(): FinancialProductOptionGrpc {
        return FinancialProductOptionGrpc.newBuilder()
            .setInterestRateType(this.interestRateType.name)
            .setReserveType(this.reserveType?.let { StringValue.of(it.name) } ?:  StringValue.newBuilder().build())
            .setDepositPeriodMonths(this.depositPeriodMonths)
            .setBaseInterestRate(this.baseInterestRate?.let { DoubleValue.of(it.toDouble()) } ?: DoubleValue.newBuilder().build())
            .setMaximumInterestRate(this.maximumInterestRate?.let { DoubleValue.of(it.toDouble()) ?: DoubleValue.newBuilder().build() })
            .build()
    }
}
