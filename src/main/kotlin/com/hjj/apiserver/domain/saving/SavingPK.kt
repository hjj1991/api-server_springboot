package com.hjj.apiserver.domain.saving


import java.io.Serializable

data class SavingPK(
    val bank: String,
    val finPrdtCd: String,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 5473249419463926971L
    }

}