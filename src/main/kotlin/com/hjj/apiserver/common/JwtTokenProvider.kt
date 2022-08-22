package com.hjj.apiserver.common

import com.hjj.apiserver.domain.user.User
import com.hjj.apiserver.util.logger
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
        @Value("\${spring.jwt.secret}")
        var secretKey: String
) {
    private val log = logger()
    companion object{
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        const val AUTHORIZATION_REFRESH_HEADER = "refresh_token"
        const val TOKEN_VALID_TIME: Long = 1000L * 60 * 20
        const val REFRESH_TOKEN_VALID_TIME: Long = 1000L * 3600 * 24 * 14 // 2주 동안만 토큰 유효

    }

    init {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }


    fun createToken(user: User, tokenType: TokenType): String{
        val claims = Jwts.claims()
        claims.subject = user.userNo.toString()
        var validTime = Date(Date().time + REFRESH_TOKEN_VALID_TIME)
        if(tokenType == TokenType.ACCESS_TOKEN){
            claims["userRole"] = user.role
            claims["userId"] = user.userId
            validTime = Date(Date().time + TOKEN_VALID_TIME)
        }


        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuedAt(Date())
                .setExpiration(validTime)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact()
    }

    /* Jwt 토큰으로 인증 정보 조회 */
    fun getAuthentication(token: String): Authentication {
        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body

    }

}

enum class TokenType{
    ACCESS_TOKEN,
    REFRESH_TOKEN,
}