package com.hjj.apiserver.domain.saving

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class SavingPK(
    var bank: String,
    var finPrdtCd: String,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 5473249419463926971L
    }
}
