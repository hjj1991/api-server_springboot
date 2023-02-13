package com.hjj.apiserver.domain.saving


import java.io.Serializable
import jakarta.persistence.Embeddable

@Embeddable
data class SavingPK(
    var bank: String,
    var finPrdtCd: String,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 5473249419463926971L
    }

}