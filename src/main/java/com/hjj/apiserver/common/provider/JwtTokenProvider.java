package com.hjj.apiserver.common.provider;

import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.TokenDto;
import com.hjj.apiserver.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider  {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_REFRESH_HEADER = "refresh_token";
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("spring.jwt.secret")
    private String secretKey;

    private long tokenValidMilisecond = 1000L * 60 * 20; // 20분만 토큰 유효
    private long refreshTokenValidMilisecond = 1000L * 3600 * 24 * 14; // 2주 동안만 토큰 유효
    private final CustomUserDetailsService customUserDetailsService;


    public enum TokenKey{
        EXPIRETIME, TOKEN;
    }


    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Jwt AcessToken 생성
    public HashMap<TokenKey, Object> createToken(UserEntity userEntity) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userEntity.getUserNo()));
        claims.put("userRole", userEntity.getRole());
        claims.put("userId", userEntity.getUserId());

        Date now = new Date();
        long expireTime = new Date().getTime() + tokenValidMilisecond;
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims) // 데이터
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(expireTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();

        HashMap<TokenKey, Object> result = new HashMap<>();
        result.put(TokenKey.EXPIRETIME, expireTime);
        result.put(TokenKey.TOKEN, token);

        return result;

    }

    // Jwt RefreshToken 생성
    public String createRefreshToken(UserEntity userEntity) {
        Claims claims = Jwts.claims().setSubject(AUTHORIZATION_REFRESH_HEADER);
        claims.put("userNo", userEntity.getUserNo());

        long expireTime = new Date().getTime() + refreshTokenValidMilisecond;
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims) // 데이터
                .setIssuedAt(new Date()) // 토큰 발행일자
                .setExpiration(new Date(expireTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();


        return token;

    }

    // Jwt 토큰으로 인증 정보를 조회
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        UserEntity userEntity = (UserEntity) customUserDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userEntity, "", userEntity.getAuthorities());
    }

    // Jwt 토큰으로 TokenDto 객체 반환
    public TokenDto getTokenDto(String token) {
        return new TokenDto(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody());
    }

    /*
        Request의 Header에서 token 파싱
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }


    // Jwt 액세스토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}