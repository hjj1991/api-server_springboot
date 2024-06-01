package com.hjj.apiserver.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    @Column(columnDefinition = "boolean default false", nullable = false, insertable = false)
    var isDelete: Boolean = false,
    @CreatedBy
    @Column(updatable = false)
    var createdBy: Long? = null,
    @LastModifiedBy
    @Column
    var lastModifiedBy: Long? = null,
) : BaseTimeEntity() {
    fun delete() {
        isDelete = true
    }
}
