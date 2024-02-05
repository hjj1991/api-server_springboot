package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.domain.user.LogType
import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_user_log")
class UserLogEntity(
    loginDateTime: LocalDateTime? = null,
    logType: LogType,
    userEntity: UserEntity,
    createdDate: LocalDateTime = LocalDateTime.now()
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userLogNo: Long = 0L

    @Column
    val loginDateTime: LocalDateTime? = loginDateTime

    @Column
    @Enumerated(EnumType.STRING)
    val logType: LogType = logType

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val userEntity: UserEntity = userEntity

    @Column
    val createdDate: LocalDateTime = createdDate
}