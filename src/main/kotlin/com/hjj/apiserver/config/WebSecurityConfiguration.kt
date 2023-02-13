package com.hjj.apiserver.config

import com.hjj.apiserver.common.*
import com.hjj.apiserver.common.filter.JwtAuthenticationFilter
import com.hjj.apiserver.service.CustomOauth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsUtils

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(
    private val jwtTokenProvider: JwtTokenProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val customOauth2UserService: CustomOauth2UserService,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler,
    private val clientRegistrationRepository: ClientRegistrationRepository,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.httpBasic().disable()
            .formLogin().disable()
            .csrf().disable()
            .headers().frameOptions().disable()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .cors()
            .and()
            .oauth2Login()
            .authorizationEndpoint()
            .authorizationRequestResolver(CustomAuthorizationRequestResolver(this.clientRegistrationRepository))
            .and()
            .tokenEndpoint()
            .accessTokenResponseClient(CustomAuthorizationCodeTokenResponseClient())
            .and()
            .userInfoEndpoint() // 소셜로그인 성공 시 후속 조치를 진행할 UserService 인터페이스 구현체 등록
            .userService(customOauth2UserService)
            .and()
            .successHandler(oAuth2SuccessHandler)
            .and()
            .authorizeHttpRequests()
            .requestMatchers(RequestMatcher { CorsUtils.isPreFlightRequest(it) })
            .permitAll()
            .requestMatchers(
                "/static/**",
                "/user/*/exists*",
                "/main*",
                "/deposit*",
                "/saving*",
                "/user/signup",
                "/user/signin",
                "/user/social/signin",
                "/user/social/signup",
                "/user/oauth/token",
                "/user/profile*",
                "/h2-console/**"
            ).permitAll() // 가입 및 인증 주소는 누구나 접근가능
            //				.antMatchers("/v1/*").hasRole("MASTER")
            //				.antMatchers(HttpMethod.POST, "/v1/*").permitAll() // 가입 및 인증 주소는 누구나 접근가능
            //				.antMatchers(HttpMethod.GET, "/v1/*").permitAll() // hellowworld로 시작하는 GET요청 리소스는 누구나 접근가능
            //				.antMatchers(HttpMethod.PUT, "/v1/*").permitAll() // hellowworld로 시작하는 GET요청 리소스는 누구나 접근가능
            //				.antMatchers(HttpMethod.POST, "/*/board/**").permitAll() // hellowworld로 시작하는 GET요청 리소스는 누구나 접근가능
            .anyRequest().hasRole("USER") // 그외 나머지 요청은 모두 인증된 회원만 접근 가능
            //                .anyRequest().hasAnyRole( "MASTER")
            .and()
            .exceptionHandling().accessDeniedHandler(AccessDeniedHandlerImpl())
            .and()
            .exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint)
            .and()
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),  // jwt token 필터를 id/password 인증 필터 전에 넣는다
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(
                "/docs/**", "/swagger-resources/**", "/swagger-ui/swagger-ui.html", "/webjars/**",
                "/swagger/**", "/swagger-ui/**"
            )
        }
    }


}