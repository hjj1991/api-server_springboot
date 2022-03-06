package com.hjj.apiserver.dto;

import com.hjj.apiserver.domain.UserEntity;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
public class UserDto {

    private Long userNo;
    private String userId;
    private String name;
    private String nickName;
    private String userEmail;
    private String userPw;
    private String picture;
    private String provider;
    private LocalDateTime loginDateTime;

    public UserEntity toEntity() {
        return UserEntity.builder()
                .userId(userId)
                .name(name)
                .nickName(nickName)
                .userEmail(userEmail)
                .role(UserEntity.Role.USER)
                .userPw(userPw)
                .picture(picture)
                .provider(provider)
                .loginDateTime(loginDateTime)
                .build();
    }

    public UserEntity toEntityWithPasswordEncode(PasswordEncoder bCryptPasswordEncoder){
        return UserEntity.builder()
                .userId(userId)
                .name(name)
                .nickName(nickName)
                .userEmail(userEmail)
                .role(UserEntity.Role.USER)
                .userPw(bCryptPasswordEncoder.encode(userPw))
                .picture(picture)
                .provider(provider)
                .loginDateTime(loginDateTime)
                .build();
    }

    @Data
    public static class RequestSignUpForm {

        private String userId;
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$", message ="공백제외 한글, 영문, 숫자 2 ~ 10자로 입력해주세요.")
        private String name;
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$", message ="공백제외 한글, 영문, 숫자 2 ~ 10자로 입력해주세요.")
        private String nickName;
        @Email(message = "잘못된 이메일 주소입니다.")
        private String userEmail;
        @Pattern(regexp = "^[a-zA-Z0-9~!@#$%^&*()_+|<>?:{}]{7,14}$", message ="비밀번호는 영문 숫자 조합 7 ~ 14자리 이상입니다.")
        private String userPw;
        private String picture;
        private String provider;
        private LocalDateTime loginDateTime;
    }

    @Data
    public static class ResponseSignIn {
        private String userId;
        private String name;
        private String nickName;
        private String userEmail;
        private String picture;
        private String provider;
        private LocalDateTime createDate;
        private LocalDateTime loginDateTime;
        private String accessToken;
        private String expireTime;
    }

    @Getter
    public static class RequestSignInForm {
        private String userId;
        private String userPw;
    }
}
