package com.hjj.apiserver.domain.user

import org.springframework.util.StringUtils

enum class Provider {
    GENERAL,
    NAVER,
    KAKAO,
    ;

    companion object {
        fun isExist(name: String?): Boolean {
            if (!StringUtils.hasText(name)) {
                return false
            }

            for (value in Provider.values()) {
                if (name == value.name) {
                    return true
                }
            }

            return false
        }
    }
}
