package com.hjj.apiserver.adapter.out.persistence.auth.entity

import com.hjj.apiserver.adapter.out.persistence.BaseTimeEntity
import com.hjj.apiserver.domain.auth.UserDeletionRequestStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(
    name = "user_deletion_requests",
    indexes = [
        Index(name = "ix_user_deletion_requests__user_id_requested_at", columnList = "user_id,requested_at"),
        Index(
            name = "ix_user_deletion_requests__status_scheduled_purge_at",
            columnList = "status,scheduled_purge_at",
        ),
    ],
)
class UserDeletionRequestEntity(
    id: Long = 0L,
    userId: Long,
    status: UserDeletionRequestStatus,
    requestedAt: OffsetDateTime,
    scheduledPurgeAt: OffsetDateTime,
    cancelledAt: OffsetDateTime? = null,
    purgedAt: OffsetDateTime? = null,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = id
        protected set

    @Column(name = "user_id")
    var userId: Long = userId
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    var status: UserDeletionRequestStatus = status
        protected set

    @Column(name = "requested_at", columnDefinition = "timestamptz")
    var requestedAt: OffsetDateTime = requestedAt
        protected set

    @Column(name = "scheduled_purge_at", columnDefinition = "timestamptz")
    var scheduledPurgeAt: OffsetDateTime = scheduledPurgeAt
        protected set

    @Column(name = "cancelled_at", columnDefinition = "timestamptz")
    var cancelledAt: OffsetDateTime? = cancelledAt
        protected set

    @Column(name = "purged_at", columnDefinition = "timestamptz")
    var purgedAt: OffsetDateTime? = purgedAt
        protected set
}
