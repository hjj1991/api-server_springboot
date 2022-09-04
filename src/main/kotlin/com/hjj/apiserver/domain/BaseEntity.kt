package com.hjj.apiserver.domain

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
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