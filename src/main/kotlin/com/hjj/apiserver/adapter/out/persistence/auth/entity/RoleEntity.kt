package com.hjj.apiserver.adapter.out.persistence.auth.entity

import com.hjj.apiserver.adapter.out.persistence.BaseTimeEntity
import com.hjj.apiserver.domain.auth.RoleName
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "roles",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_roles__role_name", columnNames = ["role_name"]),
    ],
)
class RoleEntity(
    id: Long = 0L,
    roleName: RoleName,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = id
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", length = 30)
    var roleName: RoleName = roleName
        protected set
}
