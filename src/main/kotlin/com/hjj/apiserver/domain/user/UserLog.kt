package com.hjj.apiserver.domain.user

import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "tb_user_log")
class UserLog(
    loginDateTime: LocalDateTime? = null,
    signInType: SignInType? = null,
    logType: LogType,
    user: User,
    createdDate: LocalDateTime = LocalDateTime.now()

) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val logNo: Long? = null

    @Column
    val loginDateTime: LocalDateTime? = loginDateTime

    @Column
    @Enumerated(EnumType.STRING)
    val signInType: SignInType? = signInType

    @Column
    @Enumerated(EnumType.STRING)
    val logType: LogType = logType

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo")
    val user: User = user

    @Column
    val createdDate: LocalDateTime = createdDate
}