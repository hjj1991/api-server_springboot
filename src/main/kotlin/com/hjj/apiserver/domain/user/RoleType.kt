package com.hjj.apiserver.domain.user

enum class RoleType(
    val code: String,
    val displayTitle: String,
) {
    GUEST("ROLE_GUEST", "손님"),
    USER("ROLE_USER", "일반사용자"),
    ADMIN("ROLE_ADMIN", "관리자"),
}
