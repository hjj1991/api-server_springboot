package com.hjj.apiserver.adapter.out.persistence.user

import com.hjj.apiserver.adapter.out.persistence.user.entity.RefreshTokenEntity
import com.hjj.apiserver.adapter.out.persistence.user.entity.UserLogEntity
import com.hjj.apiserver.adapter.out.persistence.user.entity.UserRoleEntity
import com.hjj.apiserver.adapter.out.persistence.user.entity.toUserEntity
import com.hjj.apiserver.adapter.out.persistence.user.repository.RefreshTokenRepository
import com.hjj.apiserver.adapter.out.persistence.user.repository.RoleRepository
import com.hjj.apiserver.adapter.out.persistence.user.repository.UserLogRepository
import com.hjj.apiserver.adapter.out.persistence.user.repository.UserRepository
import com.hjj.apiserver.adapter.out.persistence.user.repository.UserRoleRepository
import com.hjj.apiserver.application.port.out.user.GetRolePort
import com.hjj.apiserver.application.port.out.user.GetUserPort
import com.hjj.apiserver.application.port.out.user.GetUserRolePort
import com.hjj.apiserver.application.port.out.user.WriteRefreshTokenPort
import com.hjj.apiserver.application.port.out.user.WriteUserLogPort
import com.hjj.apiserver.application.port.out.user.WriteUserPort
import com.hjj.apiserver.application.port.out.user.WriteUserRolePort
import com.hjj.apiserver.common.ErrConst
import com.hjj.apiserver.common.PersistenceAdapter
import com.hjj.apiserver.common.exception.NotFoundException
import com.hjj.apiserver.converter.toRefreshToken
import com.hjj.apiserver.converter.toRole
import com.hjj.apiserver.converter.toUser
import com.hjj.apiserver.converter.toUserLog
import com.hjj.apiserver.converter.toUserRole
import com.hjj.apiserver.domain.user.RefreshToken
import com.hjj.apiserver.domain.user.Role
import com.hjj.apiserver.domain.user.RoleType
import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.domain.user.UserLog
import com.hjj.apiserver.domain.user.UserRole
import org.springframework.data.repository.findByIdOrNull

@PersistenceAdapter
class UserPersistenceAdapter(
    val userRepository: UserRepository,
    val userRoleRepository: UserRoleRepository,
    val userLogRepository: UserLogRepository,
    val roleRepository: RoleRepository,
    val refreshTokenRepository: RefreshTokenRepository,
) : GetUserPort, WriteUserPort, GetRolePort, GetUserRolePort, WriteUserRolePort, WriteUserLogPort, WriteRefreshTokenPort {
    override fun existsUserNickName(nickName: String): Boolean {
        return userRepository.existsByUserNickName(nickName)
    }

    override fun findById(userId: Long): User {
        val userEntity = userRepository.findByIdOrNull(userId) ?: throw NotFoundException(ErrConst.ERR_CODE0014)
        return userEntity.toUser()
    }

    override fun findByUsername(username: String): User {
        val userEntity = userRepository.findByUsername(username) ?: throw NotFoundException(ErrConst.ERR_CODE0001)
        return userEntity.toUser()
    }

    override fun insertUser(user: User): User {
        val userEntity = userRepository.save(user.toUserEntity())
        return userEntity.toUser()
    }

    override fun findByRoleType(roleType: RoleType): Role {
        val roleEntity = (roleRepository.findByRoleType(roleType)
            ?: throw NotFoundException(ErrConst.ERR_CODE0015))
        return roleEntity.toRole()
    }

    override fun insertUserRole(userRole: UserRole): UserRole {
        val userRoleEntity = userRoleRepository.save(
            UserRoleEntity(
                userEntity = userRepository.getReferenceById(userRole.userId),
                roleEntity = roleRepository.getReferenceById(userRole.roleId),
            )
        )
        return userRoleEntity.toUserRole()
    }

    override fun insertUserLog(userLog: UserLog): UserLog {
        val userLogEntity = userLogRepository.save(
            UserLogEntity(
                logType = userLog.logType,
                userEntity = userRepository.getReferenceById(userLog.userId)
            )
        )
        return userLogEntity.toUserLog()
    }

    override fun findRolesByUserId(userId: Long): Set<Role> {
        val userRoleEntities = userRoleRepository.findByUserEntityId(userId)
        return userRoleEntities.map { it.roleEntity.toRole() }.toSet()
    }

    override fun revokeAllTokensByUserId(userId: Long): Int =
        refreshTokenRepository.revokeAllTokensByUserId(userId)

    override fun insertRefreshToken(refreshToken: RefreshToken): RefreshToken {
        val refreshTokenEntity = refreshTokenRepository.save(
            RefreshTokenEntity(
                jti = refreshToken.jti,
                userId = refreshToken.userId,
                issuedAt = refreshToken.issuedAt,
                expiresAt = refreshToken.expiresAt,
                revoked = refreshToken.revoked,
                userAgent = refreshToken.userAgent,
            )
        )
        return refreshTokenEntity.toRefreshToken()
    }
}
