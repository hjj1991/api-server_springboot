package com.hjj.apiserver.adapter.input.web.auth.request

import com.hjj.apiserver.application.port.input.auth.LinkSocialIdentityCommand
import com.hjj.apiserver.domain.auth.AuthProviderType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SocialLinkIdentityRequest(
    @field:NotNull
    val providerType: AuthProviderType?,
    @field:NotBlank
    val providerSubject: String?,
) {
    fun toCommand(userId: Long): LinkSocialIdentityCommand =
        LinkSocialIdentityCommand(
            userId = userId,
            providerType = requireNotNull(providerType),
            providerSubject = requireNotNull(providerSubject),
        )
}
