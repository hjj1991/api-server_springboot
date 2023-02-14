package com.hjj.apiserver.dto.accountbook.request

import jakarta.validation.constraints.NotBlank

data class AccountBookAddRequest(
    @field:NotBlank
    val accountBookName: String,
    @field:NotBlank
    val accountBookDesc: String,
    @field:NotBlank
    val backGroundColor: String,
    @field:NotBlank
    val color: String,
) {
}