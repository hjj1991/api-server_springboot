package com.hjj.apiserver.adapter.out.persistence.user.entity

import com.hjj.apiserver.domain.user.Provider
import com.hjj.apiserver.domain.user.SnsAccountStatusType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "sns_account")
class SnsAccountEntity(
    snsId: String,
    snsEmail: String?,
    provider: Provider,
    user: UserEntity,
    state: SnsAccountStatusType,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
        protected set

    @Column(nullable = false)
    var snsId: String = snsId
        protected set

    @Column
    var snsEmail: String? = snsEmail
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var provider: Provider = provider
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: UserEntity = user
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var statusType: SnsAccountStatusType = state
        protected set

}
