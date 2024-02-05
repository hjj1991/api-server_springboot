package com.hjj.apiserver.common

import com.hjj.apiserver.adapter.out.persistence.user.UserEntity
import com.hjj.apiserver.service.impl.CustomUserDetailService
import com.hjj.apiserver.util.logger
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.*

@Component
class JwtTokenProvider(
    private val userDetailService: CustomUserDetailService,
    @Value("\${spring.jwt.secret}")
    var secretKey: String,
) {
    private val log = logger()
    companion object{
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        const val TOKEN_VALID_TIME: Long = 1000L * 60 * 20
        const val REFRESH_TOKEN_VALID_TIME: Long = 1000L * 3600 * 24 * 14 // 2주 동안만 토큰 유효

    }

    init {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
    }


    fun createToken(userEntity: UserEntity, tokenType: TokenType): String{
        val claims = Jwts.claims()
        claims.subject = userEntity.userNo.toString()
        var validTime = Date(Date().time + REFRESH_TOKEN_VALID_TIME)
        if(tokenType == TokenType.ACCESS_TOKEN){
            claims["userRole"] = userEntity.role
//            claims["userId"] = userEntity.userId
            claims["userId"] = userEntity.userEmail
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
        val user = userDetailService.loadUserByUsername(claims.subject)
        return UsernamePasswordAuthenticationToken(user, "", user.authorities)
    }

    fun getUserNoByToken(token: String): Long {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject.toLong()
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
    fun validateToken(token: String): Boolean{
        return try{
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            !claims.body.expiration.before(Date())
        }catch (e: Exception){
            false
        }
    }

}

enum class TokenType{
    ACCESS_TOKEN,
    REFRESH_TOKEN,
}