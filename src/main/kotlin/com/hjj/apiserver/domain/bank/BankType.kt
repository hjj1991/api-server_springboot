package com.hjj.apiserver.domain.bank

enum class BankType(
    val topFinGrpNo: String,
    val title: String,
) {
    BANK("020000", "은행"),
    SAVING_BANK("030300", "저축은행"),
}