package com.hjj.apiserver.domain.financial

import org.slf4j.LoggerFactory

enum class ReserveType(val code: String, val description: String) {
    FLEXIBLE("F", "자유적립식"),
    FIXED("S", "정액적립식"),
    ;

    companion object {
        private val log = LoggerFactory.getLogger(ReserveType::class.java)

        fun fromCode(code: String?): ReserveType? {
            if (code.isNullOrBlank()) {
                return null
            }
            for (type in entries) {
                if (type.code == code) {
                    return type
                }
            }
            log.warn("알 수 없는 reserve type 코드가 들어왔습니다. code={}", code)
            return null
        }
    }
}
