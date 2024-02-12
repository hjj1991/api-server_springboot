package com.hjj.apiserver.common.filter

import com.hjj.apiserver.common.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter


class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
//        val resolveToken = jwtTokenProvider.resolveToken(request)
//        if(StringUtils.hasText(resolveToken) && jwtTokenProvider.validateToken(resolveToken!!)){
//            SecurityContextHolder.getContext().authentication = jwtTokenProvider.getAuthentication(resolveToken)
//        }
        filterChain.doFilter(request, response)
    }
}