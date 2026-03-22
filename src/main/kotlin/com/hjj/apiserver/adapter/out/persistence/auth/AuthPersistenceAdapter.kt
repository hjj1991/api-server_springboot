package com.hjj.apiserver.adapter.out.persistence.auth

import com.hjj.apiserver.adapter.out.persistence.auth.entity.AuthIdentityEntity
import com.hjj.apiserver.adapter.out.persistence.auth.entity.LocalCredentialEntity
import com.hjj.apiserver.adapter.out.persistence.auth.entity.RoleEntity
import com.hjj.apiserver.adapter.out.persistence.auth.entity.UserEntity
import com.hjj.apiserver.adapter.out.persistence.auth.entity.UserRoleEntity
import com.hjj.apiserver.adapter.out.persistence.auth.repository.AuthIdentityRepository
import com.hjj.apiserver.adapter.out.persistence.auth.repository.LocalCredentialRepository
import com.hjj.apiserver.adapter.out.persistence.auth.repository.RoleRepository
import com.hjj.apiserver.adapter.out.persistence.auth.repository.UserRepository
import com.hjj.apiserver.adapter.out.persistence.auth.repository.UserRoleRepository
import com.hjj.apiserver.application.port.out.auth.CheckDuplicatedEmailPort
import com.hjj.apiserver.application.port.out.auth.CheckDuplicatedLoginIdPort
import com.hjj.apiserver.application.port.out.auth.CreateSocialUserAccountPort
import com.hjj.apiserver.application.port.out.auth.CreateLocalUserAccountPort
import com.hjj.apiserver.application.port.out.auth.IncreaseStoredUserTokenVersionPort
import com.hjj.apiserver.application.port.out.auth.LinkSocialIdentityPort
import com.hjj.apiserver.application.port.out.auth.LoadAuthenticatedUserByEmailLookupHashPort
import com.hjj.apiserver.application.port.out.auth.LoadAuthenticatedUserPort
import com.hjj.apiserver.application.port.out.auth.LoadLinkedAuthIdentitiesPort
import com.hjj.apiserver.application.port.out.auth.LoadLocalLoginAccountPort
import com.hjj.apiserver.application.port.out.auth.LoadSocialAuthenticatedUserPort
import com.hjj.apiserver.application.port.out.auth.LoadStoredUserTokenVersionPort
import com.hjj.apiserver.application.port.out.auth.RecordLocalLoginSuccessPort
import com.hjj.apiserver.application.port.out.auth.model.CreateLocalUserAccountCommand
import com.hjj.apiserver.application.port.out.auth.model.CreateSocialUserAccountCommand
import com.hjj.apiserver.application.port.out.auth.model.LinkSocialIdentityAccountCommand
import com.hjj.apiserver.common.ErrConst
import com.hjj.apiserver.common.exception.NotFoundException
import com.hjj.apiserver.domain.auth.AuthenticatedUserAccount
import com.hjj.apiserver.domain.auth.AuthProviderType
import com.hjj.apiserver.domain.auth.AuthUser
import com.hjj.apiserver.domain.auth.LinkedAuthIdentity
import com.hjj.apiserver.domain.auth.LocalLoginAccount
import com.hjj.apiserver.domain.auth.RoleName
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class AuthPersistenceAdapter(
    private val userRepository: UserRepository,
    private val authIdentityRepository: AuthIdentityRepository,
    private val localCredentialRepository: LocalCredentialRepository,
    private val roleRepository: RoleRepository,
    private val userRoleRepository: UserRoleRepository,
) : LoadLocalLoginAccountPort,
    RecordLocalLoginSuccessPort,
    LoadAuthenticatedUserPort,
    CheckDuplicatedEmailPort,
    CheckDuplicatedLoginIdPort,
    CreateLocalUserAccountPort,
    CreateSocialUserAccountPort,
    LoadStoredUserTokenVersionPort,
    IncreaseStoredUserTokenVersionPort,
    LoadSocialAuthenticatedUserPort,
    LoadAuthenticatedUserByEmailLookupHashPort,
    LinkSocialIdentityPort,
    LoadLinkedAuthIdentitiesPort {
    override fun loadByLoginId(loginId: String): LocalLoginAccount? {
        val localIdentity = authIdentityRepository.findByProviderTypeAndLoginId(AuthProviderType.LOCAL, loginId) ?: return null
        val userEntity = localIdentity.userEntity
        val credential = localCredentialRepository.findByUserEntityId(userEntity.id) ?: return null

        return LocalLoginAccount(
            user = userEntity.toDomain(),
            identityStatus = localIdentity.status,
            passwordHash = credential.passwordHash,
            lockedUntil = credential.lockedUntil,
            roleNames = loadRoleNames(userEntity.id),
        )
    }

    override fun record(
        userId: Long,
        loggedInAt: OffsetDateTime,
    ) {
        authIdentityRepository.findByUserEntityIdAndProviderType(userId, AuthProviderType.LOCAL)
            ?.markLoggedIn(loggedInAt)
    }

    override fun load(userId: Long): AuthenticatedUserAccount? {
        val userEntity = userRepository.findById(userId).orElse(null) ?: return null
        return AuthenticatedUserAccount(
            user = userEntity.toDomain(),
            roleNames = loadRoleNames(userId),
        )
    }

    override fun loadByProviderSubjectHash(
        providerType: AuthProviderType,
        providerSubjectHash: String,
    ): AuthenticatedUserAccount? {
        val identity =
            authIdentityRepository.findByProviderTypeAndProviderSubjectHash(providerType, providerSubjectHash)
                ?: return null

        val userEntity = identity.userEntity
        return AuthenticatedUserAccount(
            user = userEntity.toDomain(),
            roleNames = loadRoleNames(userEntity.id),
        )
    }

    override fun loadActiveByEmailLookupHash(emailLookupHash: String): AuthenticatedUserAccount? {
        val userEntity = userRepository.findByEmailLookupHash(emailLookupHash) ?: return null
        if (userEntity.status != com.hjj.apiserver.domain.auth.UserStatus.ACTIVE) {
            return null
        }

        return AuthenticatedUserAccount(
            user = userEntity.toDomain(),
            roleNames = loadRoleNames(userEntity.id),
        )
    }

    override fun existsByEmailLookupHash(emailLookupHash: String): Boolean = userRepository.existsByEmailLookupHash(emailLookupHash)

    override fun existsByLoginId(loginId: String): Boolean =
        authIdentityRepository.existsByProviderTypeAndLoginId(AuthProviderType.LOCAL, loginId)

    override fun create(command: CreateLocalUserAccountCommand) {
        val userEntity =
            userRepository.save(
                UserEntity(
                    displayName = command.displayName,
                    emailCiphertext = command.encryptedEmail,
                    emailLookupHash = command.emailLookupHash,
                    emailVerifiedAt = command.emailVerifiedAt,
                ),
            )

        authIdentityRepository.save(
            AuthIdentityEntity(
                userEntity = userEntity,
                providerType = AuthProviderType.LOCAL,
                loginId = command.loginId,
                providerSubjectHash = command.providerSubjectHash,
                linkedAt = command.emailVerifiedAt,
            ),
        )

        localCredentialRepository.save(
            LocalCredentialEntity(
                userEntity = userEntity,
                passwordHash = command.passwordHash,
                passwordAlgo = command.passwordAlgo,
                passwordUpdatedAt = command.passwordUpdatedAt,
            ),
        )

        val userRole =
            roleRepository.findByRoleName(RoleName.USER)
                ?: roleRepository.save(RoleEntity(roleName = RoleName.USER))

        userRoleRepository.save(
            UserRoleEntity(
                userEntity = userEntity,
                roleEntity = userRole,
            ),
        )
    }

    override fun create(command: CreateSocialUserAccountCommand): AuthenticatedUserAccount {
        val userEntity =
            userRepository.save(
                UserEntity(
                    displayName = command.displayName,
                    emailCiphertext = command.encryptedEmail,
                    emailLookupHash = command.emailLookupHash,
                    emailVerifiedAt = command.emailVerifiedAt,
                ),
            )

        authIdentityRepository.save(
            AuthIdentityEntity(
                userEntity = userEntity,
                providerType = command.providerType,
                providerSubjectHash = command.providerSubjectHash,
                linkedAt = command.linkedAt,
            ),
        )

        val userRole =
            roleRepository.findByRoleName(RoleName.USER)
                ?: roleRepository.save(RoleEntity(roleName = RoleName.USER))

        userRoleRepository.save(
            UserRoleEntity(
                userEntity = userEntity,
                roleEntity = userRole,
            ),
        )

        return AuthenticatedUserAccount(
            user = userEntity.toDomain(),
            roleNames = listOf(RoleName.USER),
        )
    }

    override fun link(command: LinkSocialIdentityAccountCommand): AuthenticatedUserAccount {
        val userEntity =
            userRepository.findById(command.userId).orElseThrow {
                NotFoundException(ErrConst.ERR_CODE0001)
            }

        authIdentityRepository.save(
            AuthIdentityEntity(
                userEntity = userEntity,
                providerType = command.providerType,
                providerSubjectHash = command.providerSubjectHash,
                linkedAt = command.linkedAt,
            ),
        )

        return AuthenticatedUserAccount(
            user = userEntity.toDomain(),
            roleNames = loadRoleNames(userEntity.id),
        )
    }

    override fun loadTokenVersion(userId: Long): Long? = userRepository.findTokenVersionById(userId)

    override fun increaseTokenVersion(userId: Long): Long {
        val userEntity =
            userRepository.findById(userId).orElseThrow {
                NotFoundException(ErrConst.ERR_CODE0001)
            }

        return userEntity.increaseTokenVersion()
    }

    override fun loadByUserId(userId: Long): List<LinkedAuthIdentity> =
        authIdentityRepository.findAllByUserEntityIdOrderByLinkedAtAsc(userId).map { identity ->
            LinkedAuthIdentity(
                providerType = identity.providerType,
                loginId = identity.loginId,
                linkedAt = identity.linkedAt,
                lastLoginAt = identity.lastLoginAt,
            )
        }

    private fun loadRoleNames(userId: Long): List<RoleName> = userRoleRepository.findRoleNamesByUserId(userId).distinct().sorted()

    private fun UserEntity.toDomain(): AuthUser =
        AuthUser(
            userId = id,
            displayName = displayName,
            emailCiphertext = emailCiphertext,
            emailLookupHash = emailLookupHash,
            emailVerifiedAt = emailVerifiedAt,
            tokenVersion = tokenVersion,
            status = status,
        )
}
