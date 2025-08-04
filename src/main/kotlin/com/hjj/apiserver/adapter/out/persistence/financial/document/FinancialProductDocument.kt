package com.hjj.apiserver.adapter.out.persistence.financial.document

import com.hjj.apiserver.domain.financial.FinancialGroupType
import com.hjj.apiserver.domain.financial.FinancialProductType
import com.hjj.apiserver.domain.financial.JoinRestriction
import com.hjj.apiserver.domain.financial.ProductStatus
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.InnerField
import org.springframework.data.elasticsearch.annotations.MultiField

@Document(indexName = "financial_product", createIndex = false)
data class FinancialProductDocument(
    val id: String,

    @Field(type = FieldType.Long)
    val financialProductId: Long,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "nori"),
        otherFields = [InnerField(suffix = "keyword", type = FieldType.Keyword)],
    )
    val productName: String,

    @MultiField(
        mainField = Field(type = FieldType.Text, analyzer = "nori"),
        otherFields = [InnerField(suffix = "keyword", type = FieldType.Keyword)],
    )
    val companyName: String,

    @Field(type = FieldType.Keyword)
    val financialProductCode: String,

    @Field(type = FieldType.Keyword)
    val companyCode: String,

    @Field(type = FieldType.Text, analyzer = "nori")
    val specialCondition: String,

    @Field(type = FieldType.Text, analyzer = "nori")
    val joinWay: String?,

    @Field(type = FieldType.Text, analyzer = "nori")
    val etcNote: String,

    @Field(type = FieldType.Keyword)
    val financialGroupType: FinancialGroupType,

    @Field(type = FieldType.Keyword)
    val joinRestriction: JoinRestriction,

    @Field(type = FieldType.Keyword)
    val financialProductType: FinancialProductType,

    @Field(type = FieldType.Nested)
    val options: List<Option>,

    @Field(type = FieldType.Keyword)
    val status: ProductStatus,

    @Field(type = FieldType.Dense_Vector, dims = 768)
    val productVector: FloatArray,
) {
    data class Option(
        @Field(type = FieldType.Keyword)
        val interestRateType: String,

        @Field(type = FieldType.Keyword)
        val reserveType: String?,

        @Field(type = FieldType.Keyword)
        val depositPeriodMonths: String,

        @Field(type = FieldType.Double)
        val initRate: Double,

        @Field(type = FieldType.Double)
        val maxRate: Double,
    )
}
