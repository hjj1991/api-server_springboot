package com.hjj.apiserver.domain.deposit

import java.io.Serial
import java.io.Serializable

data class DepositPK(
    var bank: String,
    var finPrdCd: String,
) : Serializable {
    companion object {
        @Serial
        private const val serialVersionUID: Long = -5950103347155460286L
    }
}