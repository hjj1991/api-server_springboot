package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.domain.user.CredentialState
import com.hjj.apiserver.domain.user.Provider
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
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "tb_credential",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["userId", "provider"]),
    ],
)
class CredentialEntity(
    credentialNo: Long = 0L,
    userId: String,
    credentialEmail: String? = null,
    provider: Provider,
    userEntity: UserEntity,
    state: CredentialState,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var credentialNo: Long = credentialNo
        protected set

    @Column(length = 100, nullable = false)
    var userId: String = userId
        protected set

    @Column(length = 100)
    var credentialEmail: String? = credentialEmail
        protected set

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var provider: Provider = provider
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val userEntity: UserEntity = userEntity

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var state: CredentialState = state
}
