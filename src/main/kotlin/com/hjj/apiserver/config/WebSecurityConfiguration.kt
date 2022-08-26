package com.hjj.apiserver.config

import com.hjj.apiserver.common.CustomAuthenticationEntryPoint
import com.hjj.apiserver.common.JwtTokenProvider
import com.hjj.apiserver.common.filter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Pageable
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.cors.CorsUtils
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import javax.servlet.http.HttpServletRequest

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(
    private val jwtTokenProvider: JwtTokenProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
): WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {
        http.httpBasic().disable() // rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트 된다.
            .csrf().disable() // rest api이므로 csrf 보안이 필요없으므로 disable처리.
            .headers().frameOptions().disable()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt token으로 인증하므로 세션은 필요없으므로 생성안함.
            .and()
            .cors()
            .and()
            .authorizeRequests() // 다음 리퀘스트에 대한 사용권한 체크
            .requestMatchers(RequestMatcher { CorsUtils.isPreFlightRequest(it) })
            .permitAll()
            .antMatchers(
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
    }

    // ignore check swagger resource
    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(
            "/v2/api-docs", "/swagger-resources/**", "/swagger-ui/index.html", "/webjars/**",
            "/swagger/**", "/swagger-ui/**"
        )
    }



}