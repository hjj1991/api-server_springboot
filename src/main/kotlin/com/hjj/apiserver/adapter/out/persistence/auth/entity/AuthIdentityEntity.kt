package com.hjj.apiserver.adapter.out.persistence.auth.entity

import com.hjj.apiserver.adapter.out.persistence.BaseTimeEntity
import com.hjj.apiserver.domain.auth.AuthIdentityStatus
import com.hjj.apiserver.domain.auth.AuthProviderType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.OffsetDateTime

@Entity
@Table(
    name = "auth_identities",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_auth_identities__provider_type_subject_hash",
            columnNames = ["provider_type", "provider_subject_hash"],
        ),
        UniqueConstraint(
            name = "uq_auth_identities__provider_type_login_id",
            columnNames = ["provider_type", "login_id"],
        ),
    ],
    indexes = [
        Index(name = "ix_auth_identities__user_id_provider_type", columnList = "user_id,provider_type"),
    ],
)
class AuthIdentityEntity(
    id: Long = 0L,
    userEntity: UserEntity,
    providerType: AuthProviderType,
    loginId: String? = null,
    providerSubjectHash: String,
    status: AuthIdentityStatus = AuthIdentityStatus.ACTIVE,
    linkedAt: OffsetDateTime,
    lastLoginAt: OffsetDateTime? = null,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = id
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name = "fk_auth_identities__user"))
    var userEntity: UserEntity = userEntity
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", length = 20)
    var providerType: AuthProviderType = providerType
        protected set

    @Column(name = "login_id", length = 50)
    var loginId: String? = loginId
        protected set

    @Column(name = "provider_subject_hash", length = 64)
    var providerSubjectHash: String = providerSubjectHash
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32)
    var status: AuthIdentityStatus = status
        protected set

    @Column(name = "linked_at", columnDefinition = "timestamptz")
    var linkedAt: OffsetDateTime = linkedAt
        protected set

    @Column(name = "last_login_at", columnDefinition = "timestamptz")
    var lastLoginAt: OffsetDateTime? = lastLoginAt
        protected set

    fun markLoggedIn(loggedInAt: OffsetDateTime) {
        lastLoginAt = loggedInAt
    }
}
