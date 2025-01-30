package com.hjj.apiserver.adapter.out.persistence

import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.Clock
import java.time.ZonedDateTime

@MappedSuperclass
abstract class BaseTimeEntity() {
    var createdAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC())
    var modifiedAt: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC())

    @PrePersist
    fun prePersist() {
        this.createdAt = ZonedDateTime.now(Clock.systemUTC())
        this.modifiedAt = ZonedDateTime.now(Clock.systemUTC())
    }

    @PreUpdate
    fun preUpdate() {
        this.modifiedAt = ZonedDateTime.now(Clock.systemUTC())
    }
}
