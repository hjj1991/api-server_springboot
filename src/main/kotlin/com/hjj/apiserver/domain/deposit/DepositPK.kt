package com.hjj.apiserver.domain.deposit

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class DepositPK(
    var bank: String,
    var finPrdtCd: String,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 2379397187016502760L
    }
}
