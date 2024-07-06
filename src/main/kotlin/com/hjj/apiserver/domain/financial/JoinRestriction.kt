package com.hjj.apiserver.domain.financial

enum class JoinRestriction(private val code: Int, private val description: String) {
    NO_RESTRICTION(1, "제한없음"),
    LOW_INCOME_ONLY(2, "서민전용"),
    PARTIALLY_RESTRICTED(3, "일부제한"),
    ;

    companion object {
        fun fromCode(code: Int): JoinRestriction {
            for (restriction in entries) {
                if (restriction.code == code) {
                    return restriction
                }
            }
            throw IllegalArgumentException("Invalid code: $code")
        }
    }
}
