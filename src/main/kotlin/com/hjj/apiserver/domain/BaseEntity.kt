package com.hjj.apiserver.domain

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    @Column(columnDefinition = "char(1) default 'N'", nullable = false, insertable = false)
    var deleteYn: Char = 'N',

    @CreatedBy
    @Column(updatable = false)
    var createdBy: Long? = null,

    @LastModifiedBy
    @Column
    var lastModifiedBy: Long? = null,
): BaseTimeEntity() {




}