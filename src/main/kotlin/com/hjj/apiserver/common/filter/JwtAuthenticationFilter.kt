package com.hjj.apiserver.common.filter

import com.hjj.apiserver.common.JwtTokenProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component


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