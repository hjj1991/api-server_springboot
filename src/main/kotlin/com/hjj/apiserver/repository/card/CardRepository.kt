package com.hjj.apiserver.repository.card

import com.hjj.apiserver.domain.card.Card
import org.springframework.data.jpa.repository.JpaRepository

interface CardRepository: JpaRepository<Card, Long> {
    fun findByUserEntity_UserNoAndIsDeleteIsFalse(userNo: Long): MutableList<Card>
    fun findByCardNoAndUserEntity_UserNoAndIsDeleteIsFalse(cardNo: Long, userNo: Long): Card?
    fun findByUserEntity_UserNo(userNo: Long): MutableList<Card>
}