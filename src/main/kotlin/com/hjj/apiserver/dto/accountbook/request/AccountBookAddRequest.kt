package com.hjj.apiserver.dto.accountbook.request

data class AccountBookAddRequest(
    val accountBookName: String,
    val accountBookDesc: String,
    val backGroundColor: String,
    val color: String,
) {
}