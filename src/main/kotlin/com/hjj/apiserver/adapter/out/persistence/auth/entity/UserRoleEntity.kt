package com.hjj.apiserver.adapter.out.persistence.auth.entity

import com.hjj.apiserver.adapter.out.persistence.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "user_roles",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_user_roles__user_id_role_id", columnNames = ["user_id", "role_id"]),
    ],
    indexes = [
        Index(name = "ix_user_roles__role_id", columnList = "role_id"),
    ],
)
class UserRoleEntity(
    id: Long = 0L,
    userEntity: UserEntity,
    roleEntity: RoleEntity,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = id
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = ForeignKey(name = "fk_user_roles__user"))
    var userEntity: UserEntity = userEntity
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false, foreignKey = ForeignKey(name = "fk_user_roles__role"))
    var roleEntity: RoleEntity = roleEntity
        protected set
}
