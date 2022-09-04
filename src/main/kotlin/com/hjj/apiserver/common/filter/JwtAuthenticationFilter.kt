package com.hjj.apiserver.common.filter

import com.hjj.apiserver.common.JwtTokenProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val resolveToken = jwtTokenProvider.resolveToken(request)
        if(StringUtils.hasText(resolveToken) && jwtTokenProvider.validateToken(resolveToken!!)){
            SecurityContextHolder.getContext().authentication = jwtTokenProvider.getAuthentication(resolveToken)
        }
        filterChain.doFilter(request, response)
    }
}