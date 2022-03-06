package com.hjj.apiserver.common.provider;

import com.hjj.apiserver.domain.UserEntity;
import com.hjj.apiserver.dto.TokenDto;
import com.hjj.apiserver.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider  {

    @Autowired
    private UserService userService;
    private static final String AUTHORIZATION_HEADER = "access_token";
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("spring.jwt.secret")
    private String secretKey;

    private long tokenValidMilisecond = 1000L * 60 * 2000; // 20분만 토큰 유효



    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // Jwt 토큰 생성
    public List<String> createToken(UserEntity userEntity) {
        Claims claims = Jwts.claims().setSubject(AUTHORIZATION_HEADER);
        claims.put("userNo", userEntity.getUserNo());
        claims.put("userRole", userEntity.getRole());
        claims.put("userId", userEntity.getUserId());

        Date now = new Date();
        long expireTime = new Date().getTime() + tokenValidMilisecond;
        String token = Jwts.builder().setClaims(claims) // 데이터
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(expireTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();

        List<String> result = new ArrayList<>();

        result.add(token);
        result.add(String.valueOf(expireTime));

        return result;

    }

    // Jwt 토큰으로 인증 정보를 조회
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        TokenDto tokenDto = new TokenDto(claims);

        return new UsernamePasswordAuthenticationToken(tokenDto, "", tokenDto.getAuthorities());
    }

    /*
        Request의 Header에서 token 파싱
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken)) {
            return bearerToken;
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