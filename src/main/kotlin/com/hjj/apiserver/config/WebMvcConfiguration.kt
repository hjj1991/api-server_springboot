package com.hjj.apiserver.config

import com.hjj.apiserver.dto.user.CurrentUserInfo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Pageable
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class WebMvcConfiguration: WebMvcConfigurer {




    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000", "https://cash.sundry.ninja", "https://post-react.pages.dev")
            .allowedMethods("GET", "POST", "OPTIONS", "PUT", "DELETE", "PATCH")
            .allowCredentials(false)
            .maxAge(3600)
        super.addCorsMappings(registry)
    }

    @Bean
    fun swaggerApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .ignoredParameterTypes(CurrentUserInfo::class.java, Pageable::class.java) //제외할 파라미터
            .apiInfo(swaggerInfo()).select()
            .apis(RequestHandlerSelectors.basePackage("com.hjj.apiserver"))
            .paths(PathSelectors.any())
            .build()
            .useDefaultResponseMessages(false) // 기본으로 세팅되는 200,401,403,404 메시지를 표시 하지 않음
    }

    private fun swaggerInfo(): ApiInfo {
        return ApiInfoBuilder().title("Spring API Documentation")
            .description("앱 개발시 사용되는 서버 API에 대한 연동 문서입니다")
            .license("황재정").licenseUrl("http://localhost").version("1").build()
    }

}