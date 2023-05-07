package com.hjj.apiserver.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseTimeEntity {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(nullable = false)
    var modifiedAt: LocalDateTime = LocalDateTime.now()
}