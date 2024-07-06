package com.hjj.apiserver.domain.financial

enum class InterestRateType(private val code: String, private val description: String) {
    SIMPLE("S", "단리"),
    COMPOUND("M", "복리"),
    ;

    companion object {
        fun fromCode(code: String): InterestRateType {
            for (type in entries) {
                if (type.code == code) {
                    return type
                }
            }
            throw IllegalArgumentException("Unknown code: $code")
        }
    }
}
