package com.hjj.apiserver.domain.deposit

import java.io.Serializable

data class DepositPK(
    var bank: String,
    var finPrdCd: String,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 2379397187016502760L
    }

}