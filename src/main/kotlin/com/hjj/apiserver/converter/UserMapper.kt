package com.hjj.apiserver.converter

import com.hjj.apiserver.adapter.out.persistence.user.entity.RefreshTokenEntity
import com.hjj.apiserver.adapter.out.persistence.user.entity.RoleEntity
import com.hjj.apiserver.adapter.out.persistence.user.entity.UserEntity
import com.hjj.apiserver.adapter.out.persistence.user.entity.UserLogEntity
import com.hjj.apiserver.adapter.out.persistence.user.entity.UserRoleEntity
import com.hjj.apiserver.domain.user.RefreshToken
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.domain.user.UserLog
import com.hjj.apiserver.domain.user.UserRole

fun UserEntity.toUser(): User =
    User(
        id = id,
        username = username,
        password = password,
        nickName = nickName,
        userEmail = userEmail,
        pictureUrl = pictureUrl,
        deletedAt = deletedAt,
        createdAt = createdAt,
        modifiedAt = modifiedAt,
    )

fun RoleEntity.toRole(): Role =
    Role(
        id = id,
        roleType = roleType,
        createdAt = createdAt,
        modifiedAt = modifiedAt,
    )

fun UserRoleEntity.toUserRole(): UserRole =
    UserRole(
        id = id,
        userId = userEntity.id,
        roleId = roleEntity.id,
    )

fun UserLogEntity.toUserLog(): UserLog =
    UserLog(
        id = id,
        logType = logType,
        userId = userEntity.id,
        createdAt = createdAt,
        modifiedAt = modifiedAt,
    )

fun RefreshTokenEntity.toRefreshToken(): RefreshToken =
    RefreshToken(
        jti = jti,
        userId = userId,
        issuedAt = issuedAt,
        expiresAt = expiresAt,
        revoked = revoked,
        userAgent = userAgent,
        createdAt = createdAt,
        modifiedAt = modifiedAt
    )
