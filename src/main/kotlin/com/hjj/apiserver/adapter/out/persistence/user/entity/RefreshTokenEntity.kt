package com.hjj.apiserver.adapter.out.persistence.user.entity

import com.hjj.apiserver.adapter.out.persistence.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "refresh_token")
class RefreshTokenEntity(
    jti: String,
    userId: Long,
    issuedAt: Instant,
    expiresAt: Instant,
    revoked: Boolean,
    userAgent: String?,
) : BaseTimeEntity() {
    @Id
    @Column(name = "jti", length = 64)
    var jti: String = jti
        protected set

    @Column(name = "user_id", nullable = false)
    var userId: Long = userId
        protected set

    @Column(name = "issued_at", nullable = false)
    var issuedAt: Instant = issuedAt
        protected set

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant = expiresAt
        protected set

    @Column(name = "revoked", nullable = false)
    var revoked: Boolean = revoked
        protected set

    @Column(name = "user_agent")
    var userAgent: String? = userAgent
        protected set
}
