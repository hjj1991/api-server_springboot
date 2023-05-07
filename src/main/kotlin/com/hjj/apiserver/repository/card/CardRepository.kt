package com.hjj.apiserver.repository.card

import com.hjj.apiserver.domain.card.Card
import org.springframework.data.jpa.repository.JpaRepository

interface CardRepository: JpaRepository<Card, Long> {
    fun findByUser_UserNoAndDeleteIsFalse(userNo: Long): MutableList<Card>
    fun findByCardNoAndUser_UserNoAndDeleteIsFalse(cardNo: Long, userNo: Long): Card?
    fun findByUser_UserNo(userNo: Long): MutableList<Card>
}