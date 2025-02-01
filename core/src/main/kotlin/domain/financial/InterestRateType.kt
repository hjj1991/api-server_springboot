package domain.financial

enum class InterestRateType(val code: String, val description: String) {
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
