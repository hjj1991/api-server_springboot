package com.hjj.apiserver.application.port.input.auth

import com.hjj.apiserver.domain.auth.AuthProviderType
import java.time.OffsetDateTime

interface ManageSocialAuthUseCase {
    fun resolveLogin(command: ResolveSocialLoginCommand): SocialLoginResolutionResult

    fun linkIdentity(command: LinkSocialIdentityCommand): SocialIdentityLinkResult
}

data class ResolveSocialLoginCommand(
    val providerType: AuthProviderType,
    val providerSubject: String,
    val displayName: String?,
    val email: String?,
    val emailVerified: Boolean,
)

sealed interface SocialLoginResolutionResult {
    data class Authenticated(
        val authSession: AuthSessionResult,
        val userId: Long,
        val resolutionType: SocialAccountResolutionType,
    ) : SocialLoginResolutionResult

    data class RequiresLink(
        val providerType: AuthProviderType,
        val normalizedEmail: String,
    ) : SocialLoginResolutionResult
}

enum class SocialAccountResolutionType {
    EXISTING_IDENTITY,
    CREATED_NEW_USER,
}

data class LinkSocialIdentityCommand(
    val userId: Long,
    val providerType: AuthProviderType,
    val providerSubject: String,
)

data class SocialIdentityLinkResult(
    val userId: Long,
    val providerType: AuthProviderType,
    val linkedAt: OffsetDateTime,
)
