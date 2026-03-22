package com.hjj.apiserver.adapter.out.persistence.auth.entity

import com.hjj.apiserver.adapter.out.persistence.BaseTimeEntity
import com.hjj.apiserver.domain.auth.UserStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.OffsetDateTime

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_users__email_lookup_hash", columnNames = ["email_lookup_hash"]),
    ],
    indexes = [
        Index(name = "ix_users__status", columnList = "status"),
    ],
)
class UserEntity(
    id: Long = 0L,
    displayName: String,
    emailCiphertext: String? = null,
    emailLookupHash: String? = null,
    emailVerifiedAt: OffsetDateTime? = null,
    tokenVersion: Long = 0L,
    status: UserStatus = UserStatus.ACTIVE,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = id
        protected set

    @Column(name = "display_name", length = 100)
    var displayName: String = displayName
        protected set

    @Column(name = "email_ciphertext", columnDefinition = "text")
    var emailCiphertext: String? = emailCiphertext
        protected set

    @Column(name = "email_lookup_hash", length = 64)
    var emailLookupHash: String? = emailLookupHash
        protected set

    @Column(name = "email_verified_at", columnDefinition = "timestamptz")
    var emailVerifiedAt: OffsetDateTime? = emailVerifiedAt
        protected set

    @Column(name = "token_version")
    var tokenVersion: Long = tokenVersion
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32)
    var status: UserStatus = status
        protected set

    fun increaseTokenVersion(): Long {
        tokenVersion += 1
        return tokenVersion
    }
}
