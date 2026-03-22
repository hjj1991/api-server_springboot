package com.hjj.apiserver.adapter.input.web.auth

import com.hjj.apiserver.adapter.input.web.ApiVersionConstants
import com.hjj.apiserver.adapter.input.web.auth.request.LocalLoginRequest
import com.hjj.apiserver.adapter.input.web.auth.request.LocalSignupRequest
import com.hjj.apiserver.adapter.input.web.auth.request.LogoutRequest
import com.hjj.apiserver.adapter.input.web.auth.request.RefreshSessionRequest
import com.hjj.apiserver.adapter.input.web.auth.request.SignupVerifyRequest
import com.hjj.apiserver.adapter.input.web.auth.request.SocialLinkIdentityRequest
import com.hjj.apiserver.adapter.input.web.auth.request.SocialResolveLoginRequest
import com.hjj.apiserver.adapter.input.web.auth.response.AuthSessionResponse
import com.hjj.apiserver.adapter.input.web.auth.response.AuthenticatedUserResponse
import com.hjj.apiserver.adapter.input.web.auth.response.SignupAcceptedResponse
import com.hjj.apiserver.adapter.input.web.auth.response.SignupVerifiedResponse
import com.hjj.apiserver.adapter.input.web.auth.response.SocialIdentityLinkResponse
import com.hjj.apiserver.adapter.input.web.auth.response.SocialLoginResolutionResponse
import com.hjj.apiserver.application.port.input.auth.GetCurrentUserQuery
import com.hjj.apiserver.application.port.input.auth.LogoutAllSessionsCommand
import com.hjj.apiserver.application.port.input.auth.LogoutSessionCommand
import com.hjj.apiserver.application.port.input.auth.ManageAuthSessionUseCase
import com.hjj.apiserver.application.port.input.auth.ManageSocialAuthUseCase
import com.hjj.apiserver.application.port.input.auth.RegisterLocalSignupUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneOffset

@RestController
@RequestMapping("/auth")
class AuthController(
    private val registerLocalSignupUseCase: RegisterLocalSignupUseCase,
    private val manageAuthSessionUseCase: ManageAuthSessionUseCase,
    private val manageSocialAuthUseCase: ManageSocialAuthUseCase,
) {
    @PostMapping("/login", headers = [ApiVersionConstants.HEADER_V1])
    @ResponseStatus(HttpStatus.OK)
    fun login(
        @Valid @RequestBody request: LocalLoginRequest,
    ): AuthSessionResponse = AuthSessionResponse.from(manageAuthSessionUseCase.login(request.toCommand()))

    @PostMapping("/refresh", headers = [ApiVersionConstants.HEADER_V1])
    @ResponseStatus(HttpStatus.OK)
    fun refresh(
        @Valid @RequestBody request: RefreshSessionRequest,
    ): AuthSessionResponse = AuthSessionResponse.from(manageAuthSessionUseCase.refresh(request.toCommand()))

    @PostMapping("/signup", headers = [ApiVersionConstants.HEADER_V1])
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun signup(
        @Valid @RequestBody request: LocalSignupRequest,
    ): SignupAcceptedResponse = SignupAcceptedResponse.from(registerLocalSignupUseCase.signup(request.toCommand()))

    @PostMapping("/signup/verify", headers = [ApiVersionConstants.HEADER_V1])
    @ResponseStatus(HttpStatus.CREATED)
    fun verifySignup(
        @Valid @RequestBody request: SignupVerifyRequest,
    ): SignupVerifiedResponse = SignupVerifiedResponse.from(registerLocalSignupUseCase.verifySignup(request.toCommand()))

    @PostMapping("/social/resolve", headers = [ApiVersionConstants.HEADER_V1])
    @ResponseStatus(HttpStatus.OK)
    fun resolveSocialLogin(
        @Valid @RequestBody request: SocialResolveLoginRequest,
    ): SocialLoginResolutionResponse =
        SocialLoginResolutionResponse.from(
            manageSocialAuthUseCase.resolveLogin(request.toCommand()),
        )

    @PostMapping("/social/link", headers = [ApiVersionConstants.HEADER_V1])
    @ResponseStatus(HttpStatus.OK)
    fun linkSocialIdentity(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody request: SocialLinkIdentityRequest,
    ): SocialIdentityLinkResponse =
        SocialIdentityLinkResponse.from(
            manageSocialAuthUseCase.linkIdentity(request.toCommand(jwt.subject.toLong())),
        )

    @PostMapping("/logout", headers = [ApiVersionConstants.HEADER_V1])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody(required = false) request: LogoutRequest?,
    ) {
        manageAuthSessionUseCase.logout(
            LogoutSessionCommand(
                userId = jwt.subject.toLong(),
                accessJti = jwt.id,
                accessExpiresAt = requireNotNull(jwt.expiresAt) { "JWT expiresAt must exist." }.atOffset(ZoneOffset.UTC),
                refreshToken = request?.refreshToken,
            ),
        )
    }

    @PostMapping("/logout-all", headers = [ApiVersionConstants.HEADER_V1])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logoutAll(
        @AuthenticationPrincipal jwt: Jwt,
    ) {
        manageAuthSessionUseCase.logoutAll(
            LogoutAllSessionsCommand(
                userId = jwt.subject.toLong(),
                accessJti = jwt.id,
                accessExpiresAt = requireNotNull(jwt.expiresAt) { "JWT expiresAt must exist." }.atOffset(ZoneOffset.UTC),
            ),
        )
    }

    @GetMapping("/me", headers = [ApiVersionConstants.HEADER_V1])
    @ResponseStatus(HttpStatus.OK)
    fun me(
        @AuthenticationPrincipal jwt: Jwt,
    ): AuthenticatedUserResponse =
        AuthenticatedUserResponse.from(
            manageAuthSessionUseCase.getCurrentUser(
                GetCurrentUserQuery(
                    userId = jwt.subject.toLong(),
                ),
            ),
        )
}
