package com.hjj.apiserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KaKaoProfileDto {
    private Long id;
    private Properties properties;
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Data
    public static class Properties {
        private String nickname;
        private String thumbnail_image;
        private String profile_image;
    }

    @Data
    public static class KakaoAccount {
        private Profile profile;
        @JsonProperty("has_email")
        private Boolean hasEmail;
        private String email;
    }

    @Data
    public static class Profile {
        private String nickname;
        @JsonProperty("profile_image_url")
        private String profileImageUrl;
    }
}

