package com.hjj.apiserver.domain.user

enum class SnsAccountStatusType(
    val key: String,
    val title: String,
) {
    CONNECTED("CONNECTED", "연결됨"),
    DISCONNECTED("DISCONNECTED", "연결해제됨"),
}
