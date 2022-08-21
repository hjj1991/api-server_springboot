package com.hjj.apiserver.domain.saving

import java.io.Serial
import java.io.Serializable

data class SavingPK(
    val bank: String,
    val finPrdtCd: String,
) : Serializable {
    companion object {
        @Serial
        private const val serialVersionUID: Long = 2024642832502408822L
    }
}