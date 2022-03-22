package com.hjj.apiserver.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends BaseTimeEntity {

    @Column(columnDefinition = "char(1) default 'N'", nullable = false, insertable = false)
    private char deleteYn;

    @CreatedBy
    @Column(updatable = false)
    private Long createdBy;

    @Column
    @LastModifiedBy
    private Long lastModifiedBy;

}
