package domain.financial

enum class ReserveType(val code: String, val description: String) {
    FLEXIBLE("F", "자유적립식"),
    FIXED("S", "정액적립식"),
    ;

    companion object {
        fun fromCode(code: String): ReserveType? {
            for (type in entries) {
                if (type.code == code) {
                    return type
                }
            }
            return null
        }
    }
}
