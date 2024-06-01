package com.hjj.apiserver.repository.card

import com.hjj.apiserver.domain.card.Card
import org.springframework.data.jpa.repository.JpaRepository

interface CardRepository : JpaRepository<Card, Long> {
    fun findByUserEntityUserNoAndIsDeleteIsFalse(userNo: Long): MutableList<Card>

    fun findByCardNoAndUserEntityUserNoAndIsDeleteIsFalse(
        cardNo: Long,
        userNo: Long,
    ): Card?

    fun findByUserEntityUserNo(userNo: Long): MutableList<Card>
}
