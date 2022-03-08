package com.hjj.apiserver.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Column(columnDefinition = "char(1) default 'N'", nullable = false, insertable = false)
    private char deleteYn;

    @Column(columnDefinition = "datetime default now()", nullable = false, insertable = false)
    private LocalDateTime createDate;

    @Column
    private LocalDateTime deleteDate;

}
