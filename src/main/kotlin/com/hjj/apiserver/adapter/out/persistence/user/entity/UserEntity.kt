package com.hjj.apiserver.adapter.out.persistence.user.entity

import com.hjj.apiserver.adapter.out.persistence.BaseTimeEntity
import com.hjj.apiserver.domain.user.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Entity
@Table(name = "user")
class UserEntity(
    username: String?,
    password: String?,
    nickName: String,
    userEmail: String? = null,
    pictureUrl: String? = null,
    snsAccounts: MutableSet<SnsAccountEntity> = mutableSetOf(),
    deletedAt: ZonedDateTime? = null,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
        protected set

    @Column(unique = true)
    var username: String? = username
        protected set

    @Column
    var password: String? = password
        protected set

    @Column(nullable = false)
    var nickName: String = nickName
        protected set

    @Column
    var userEmail: String? = userEmail
        protected set

    @Column
    var pictureUrl: String? = pictureUrl
        protected set

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var snsAccounts: MutableSet<SnsAccountEntity> = snsAccounts
        protected set

    @Column
    var deletedAt: ZonedDateTime? = deletedAt
        protected set
}

fun User.toUserEntity() =
    UserEntity(
        this.username,
        this.password,
        this.nickName,
        this.userEmail,
        this.pictureUrl
    )
