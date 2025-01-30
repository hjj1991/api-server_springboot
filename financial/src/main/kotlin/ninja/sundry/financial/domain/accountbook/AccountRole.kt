package com.hjj.apiserver.domain.accountbook

enum class AccountRole {
    OWNER,
    MEMBER,
    GUEST,
    ;

    fun hasReadPermission(): Boolean = (this == MEMBER || this == OWNER)
}
