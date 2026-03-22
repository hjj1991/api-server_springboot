package com.hjj.apiserver.adapter.out.persistence.auth.entity

import com.hjj.apiserver.adapter.out.persistence.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.OffsetDateTime

@Entity
@Table(
    name = "local_credentials",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_local_credentials__user_id", columnNames = ["user_id"]),
    ],
)
class LocalCredentialEntity(
    id: Long = 0L,
    userEntity: UserEntity,
    passwordHash: String,
    passwordAlgo: String,
    passwordUpdatedAt: OffsetDateTime,
    failedAttemptCount: Int = 0,
    lockedUntil: OffsetDateTime? = null,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = id
        protected set

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name = "fk_local_credentials__user"))
    var userEntity: UserEntity = userEntity
        protected set

    @Column(name = "password_hash", length = 255)
    var passwordHash: String = passwordHash
        protected set

    @Column(name = "password_algo", length = 50)
    var passwordAlgo: String = passwordAlgo
        protected set

    @Column(name = "password_updated_at", columnDefinition = "timestamptz")
    var passwordUpdatedAt: OffsetDateTime = passwordUpdatedAt
        protected set

    @Column(name = "failed_attempt_count")
    var failedAttemptCount: Int = failedAttemptCount
        protected set

    @Column(name = "locked_until", columnDefinition = "timestamptz")
    var lockedUntil: OffsetDateTime? = lockedUntil
        protected set
}
