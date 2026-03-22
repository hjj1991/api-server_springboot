package com.hjj.apiserver.adapter.input.web.auth.response

import com.hjj.apiserver.application.port.input.auth.SocialIdentityLinkResult
import com.hjj.apiserver.application.port.input.auth.SocialLoginResolutionResult
import java.time.OffsetDateTime

data class SocialLoginResolutionResponse(
    val resultType: String,
    val userId: Long?,
    val resolutionType: String?,
    val providerType: String?,
    val normalizedEmail: String?,
    val authSession: AuthSessionResponse?,
) {
    companion object {
        fun from(result: SocialLoginResolutionResult): SocialLoginResolutionResponse =
            when (result) {
                is SocialLoginResolutionResult.Authenticated ->
                    SocialLoginResolutionResponse(
                        resultType = "AUTHENTICATED",
                        userId = result.userId,
                        resolutionType = result.resolutionType.name,
                        providerType = null,
                        normalizedEmail = null,
                        authSession = AuthSessionResponse.from(result.authSession),
                    )

                is SocialLoginResolutionResult.RequiresLink ->
                    SocialLoginResolutionResponse(
                        resultType = "REQUIRES_LINK",
                        userId = null,
                        resolutionType = null,
                        providerType = result.providerType.name,
                        normalizedEmail = result.normalizedEmail,
                        authSession = null,
                    )
            }
    }
}

data class SocialIdentityLinkResponse(
    val userId: Long,
    val providerType: String,
    val linkedAt: OffsetDateTime,
) {
    companion object {
        fun from(result: SocialIdentityLinkResult): SocialIdentityLinkResponse =
            SocialIdentityLinkResponse(
                userId = result.userId,
                providerType = result.providerType.name,
                linkedAt = result.linkedAt,
            )
    }
}
