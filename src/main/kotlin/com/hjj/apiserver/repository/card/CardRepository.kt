package com.hjj.apiserver.repository.card

import com.hjj.apiserver.domain.card.Card
import org.springframework.data.jpa.repository.JpaRepository

interface CardRepository: JpaRepository<Card, Long> {
    fun findByUser_UserNoAndDeleteYn(userNo: Long, deleteYn: Char): MutableList<Card>
    fun findByCardNoAndUser_UserNo(cardNo: Long, userNo: Long): Card?
}