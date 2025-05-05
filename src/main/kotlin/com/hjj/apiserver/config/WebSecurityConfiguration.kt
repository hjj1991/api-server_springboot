package com.hjj.apiserver.config

import com.hjj.apiserver.common.CustomAuthenticationEntryPoint
import com.hjj.apiserver.common.CustomAuthorizationCodeTokenResponseClient
import com.hjj.apiserver.common.CustomAuthorizationRequestResolver
import com.hjj.apiserver.common.JwtProvider
import com.hjj.apiserver.handler.OAuth2SuccessHandler
import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsUtils
import java.nio.charset.StandardCharsets
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(
    @Value("\${spring.jwt.secret}")
    private val jwtSecret: String,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
//    private val customOauth2UserService: CustomOauth2UserService,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler,
    private val clientRegistrationRepository: ClientRegistrationRepository,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtDecoder: JwtDecoder): SecurityFilterChain {
        http.httpBasic { httpBasic -> httpBasic.disable() }.formLogin { formLogin -> formLogin.disable() }
            .csrf { csrf -> csrf.disable() }
            .headers { headers -> headers.frameOptions { frameOption -> frameOption.disable() } }
            .sessionManagement { sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwtConfigurer ->
                    jwtConfigurer.decoder(jwtDecoder)
                    jwtConfigurer.jwtAuthenticationConverter(
                        JwtAuthenticationConverter().apply {
                            setJwtGrantedAuthoritiesConverter { jwt: Jwt ->
                                // roles 클레임을 ROLE_ 접두어 권한으로 변환
                                JwtGrantedAuthoritiesConverter().apply {
                                    setAuthoritiesClaimName(JwtProvider.CLAIM_ROLES)
                                    setAuthorityPrefix("ROLE_")
                                }.convert(jwt)
                            }
                        }
                    )
                }
            }
            .cors(Customizer.withDefaults()).oauth2Login { oauth2Login ->
                oauth2Login.authorizationEndpoint { endPoint ->
                    endPoint.authorizationRequestResolver(
                        CustomAuthorizationRequestResolver(this.clientRegistrationRepository),
                    )
                }.tokenEndpoint { tokenEndpoint ->
                    tokenEndpoint.accessTokenResponseClient(
                        CustomAuthorizationCodeTokenResponseClient(),
                    )
                }
//                    .userInfoEndpoint { userEndPoint -> userEndPoint.userService(customOauth2UserService) }
//                    .successHandler(oAuth2SuccessHandler)
            }
            .authorizeHttpRequests { authorizeHttpRequests ->
                authorizeHttpRequests.requestMatchers(
                    RequestMatcher {
                        CorsUtils.isPreFlightRequest(it)
                    },
                ).permitAll()
                    .requestMatchers(
                        AntPathRequestMatcher("/static/**"),
                        AntPathRequestMatcher("/swagger-ui/swagger-ui.html"),
                        AntPathRequestMatcher("/swagger-ui/**"),
                        AntPathRequestMatcher("/docs/**"),
                        AntPathRequestMatcher("/webjars/**"),
                        AntPathRequestMatcher("/users/exists**/**"),
                        AntPathRequestMatcher("/main*"),
                        AntPathRequestMatcher("/deposit*"),
                        AntPathRequestMatcher("/saving*"),
                        AntPathRequestMatcher("/users/sign-up"),
                        AntPathRequestMatcher("/users/sign-in"),
                        AntPathRequestMatcher("/users/nicknames/**"),
                        AntPathRequestMatcher("/user/social/signup"),
                        AntPathRequestMatcher("/user/oauth/token"),
                        AntPathRequestMatcher("/user/profile*"),
                        AntPathRequestMatcher("/test"),
                        AntPathRequestMatcher("/h2-console/**"),
                        AntPathRequestMatcher("/livez"),
                        AntPathRequestMatcher("/readyz"),
                        AntPathRequestMatcher("/financial-products"),
                        AntPathRequestMatcher("/financial-products/**"),
                    ).permitAll() // 가입 및 인증 주소는 누구나 접근가능
                    .anyRequest().hasRole("USER")
            }
            .exceptionHandling { exceptionHandling -> exceptionHandling.accessDeniedHandler(AccessDeniedHandlerImpl()) }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(
                    customAuthenticationEntryPoint,
                )
            }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun jwtEncoder(): JwtEncoder =
        NimbusJwtEncoder(ImmutableSecret(jwtSecret.toByteArray(StandardCharsets.UTF_8)))

    @Bean
    fun jwtDecoder(): JwtDecoder =
        NimbusJwtDecoder.withSecretKey(SecretKeySpec(jwtSecret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
            .build()

}
