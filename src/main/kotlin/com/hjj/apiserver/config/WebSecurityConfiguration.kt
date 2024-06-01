package com.hjj.apiserver.config

import com.hjj.apiserver.common.CustomAuthenticationEntryPoint
import com.hjj.apiserver.common.CustomAuthorizationCodeTokenResponseClient
import com.hjj.apiserver.common.CustomAuthorizationRequestResolver
import com.hjj.apiserver.common.JwtProvider
import com.hjj.apiserver.common.filter.JwtAuthenticationFilter
import com.hjj.apiserver.handler.OAuth2SuccessHandler
import com.hjj.apiserver.service.impl.CustomOauth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsUtils

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(
    private val jwtProvider: JwtProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val customOauth2UserService: CustomOauth2UserService,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler,
    private val clientRegistrationRepository: ClientRegistrationRepository,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.httpBasic { httpBasic -> httpBasic.disable() }.formLogin { formLogin -> formLogin.disable() }
            .csrf { csrf -> csrf.disable() }
            .headers { headers -> headers.frameOptions { frameOption -> frameOption.disable() } }
            .sessionManagement {
                    sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
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
                }.userInfoEndpoint { userEndPoint -> userEndPoint.userService(customOauth2UserService) }
                    .successHandler(oAuth2SuccessHandler)
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
                        AntPathRequestMatcher("/users/signup"),
                        AntPathRequestMatcher("/user/signin"),
                        AntPathRequestMatcher("/user/social/signin"),
                        AntPathRequestMatcher("/user/social/signup"),
                        AntPathRequestMatcher("/user/oauth/token"),
                        AntPathRequestMatcher("/user/profile*"),
                        AntPathRequestMatcher("/test"),
                        AntPathRequestMatcher("/h2-console/**"),
                    ).permitAll() // 가입 및 인증 주소는 누구나 접근가능
                    .anyRequest().hasRole("USER")
            }
            .exceptionHandling { exceptionHandling -> exceptionHandling.accessDeniedHandler(AccessDeniedHandlerImpl()) }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(
                    customAuthenticationEntryPoint,
                )
            }.addFilterBefore(
                // jwt token 필터를 id/password 인증 필터 전에 넣는다
                JwtAuthenticationFilter(jwtProvider),
                UsernamePasswordAuthenticationFilter::class.java,
            )

        return http.build()
    }
}
