package com.hjj.apiserver.common

import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.util.logger
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.nio.charset.StandardCharsets
import java.security.Key
import java.time.Clock
import java.util.*

@Component
class JwtProvider(
    private val clock: Clock,
    @Value("\${spring.jwt.secret}")
    var secretKey: String,
) {
    private lateinit var key: Key
    private val log = logger()
    companion object{
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        const val ACCESS_TOKEN_VALID_MILLISECONDS: Long = 1000L * 60 * 20
        const val REFRESH_TOKEN_VALID_MILLISECONDS: Long = 1000L * 3600 * 24 * 14 // 2주 동안만 토큰 유효

    }

    init {
        secretKey = Encoders.BASE64.encode(secretKey.toByteArray())
    }


    fun createToken(user: User, tokenType: TokenType): String{
        val validTime = getValidTimeByTokenType(tokenType)
        val key : Key = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))

        return Jwts.builder()
            .header().type("JWT")
            .and().claims()
            .subject(user.userNo.toString())
            .expiration(validTime)
            .issuedAt(currentDate())
            .and()
            .signWith(key)
            .compact()
    }

    /* Jwt 토큰으로 인증 정보 조회 */
    fun getAuthentication(token: String): Authentication {
//        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body
//        val user = userDetailService.loadUserByUsername(claims.subject)
        val user = User.createGuestUser()
        return UsernamePasswordAuthenticationToken(user, "", user.authorities)
    }

    fun getUserNoByToken(token: String): Long {
        return 2L
    }

    /* Request의 Header에서 token 파싱 */
    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
            return bearerToken.substring(7)
        }
        return null
    }

    /* Jwt 액세스토큰의 유효성 + 만료일자 확인 */
    fun isValidToken(token: String): Boolean{
        return try{
            val secret = Keys.hmacShaKeyFor(secretKey.toByteArray())
            val validToken = Jwts.parser().verifyWith(secret).build().parseSignedClaims(token)
            !validToken.payload.expiration.before(currentDate())
        }catch (e: Exception){
            false
        }
    }

    fun getValidTimeByTokenType(tokenType: TokenType): Date {
        return when(tokenType){
            TokenType.ACCESS_TOKEN -> Date(currentDate().time + ACCESS_TOKEN_VALID_MILLISECONDS)
            TokenType.REFRESH_TOKEN -> Date(currentDate().time + REFRESH_TOKEN_VALID_MILLISECONDS)
        }
    }

    private fun currentDate(): Date {
        return Date(clock.millis())
    }

}

enum class TokenType{
    ACCESS_TOKEN,
    REFRESH_TOKEN,
}