package com.hjj.apiserver.adapter.out.persistence

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    @CreatedBy
    @Column(updatable = false)
    var createdBy: Long? = null,
    @LastModifiedBy
    @Column
    var lastModifiedBy: Long? = null,
) : BaseTimeEntity()
