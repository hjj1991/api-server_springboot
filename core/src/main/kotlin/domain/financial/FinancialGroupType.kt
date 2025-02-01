package domain.financial

enum class FinancialGroupType(val financialGroupCode: String, val title: String) {
    BANK("020000", "은행"),
    SAVING_BANK("030300", "저축은행"),
    ;

    companion object {
        fun fromCode(code: String): FinancialGroupType {
            for (type in entries) {
                if (type.financialGroupCode == code) {
                    return type
                }
            }
            throw IllegalArgumentException("Unknown financial group code: $code")
        }

        fun fromTitle(title: String): FinancialGroupType {
            for (type in entries) {
                if (type.title.equals(title, ignoreCase = true)) {
                    return type
                }
            }
            throw IllegalArgumentException("Unknown financial group title: $title")
        }
    }
}
