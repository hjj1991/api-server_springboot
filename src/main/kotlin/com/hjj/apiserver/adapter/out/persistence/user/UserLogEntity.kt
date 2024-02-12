package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.domain.BaseTimeEntity
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

@Entity
@Table(name = "tb_user_log")
class UserLogEntity(
    userLogNo: Long = 0L,
    logType: LogType,
    userEntity: UserEntity,
):BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userLogNo: Long = userLogNo

    @Column
    @Enumerated(EnumType.STRING)
    var logType: LogType = logType

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var userEntity: UserEntity = userEntity

}