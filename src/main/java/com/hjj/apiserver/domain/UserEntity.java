package com.hjj.apiserver.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tb_user")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity implements UserDetails {

    @RequiredArgsConstructor
    @Getter
    public enum Role {
        GUEST("ROLE_GUEST", "손님"),
        USER("ROLE_USER", "일반사용자"),
        ADMIN("ROLE_ADMIN", "관리자");

        private final String key;
        private final String title;
    }

    public enum Provider {
        NAVER, KAKAO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNo;

    @Column(length = 100, nullable = false, unique = true)
    private String userId;

    @Column(length = 20, nullable = false, unique = true)
    private String nickName;

    @Column(length = 200, nullable = true)
    private String userEmail;

    @Column(length = 300, nullable = true)
    private String userPw;

    @Column(nullable = true)
    private String picture;

    @Column(nullable = true)
    private Provider provider;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userInfo")
    @Builder.Default
    private List<PurchaseEntity> purchaseEntityList = new ArrayList<>();

    @Column
    private LocalDateTime loginDateTime;

    @Column(columnDefinition = "datetime default now()", nullable = false)
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime lastModifiedDate;

    @Column(nullable = true)
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'USER'")
    private Role role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.role.getKey()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return userPw;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override   //계정이 만료가 안되었는지
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override   //계정이 잠겨있지 않은지
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override   //계정 패스워드가 만료되지 않았는지
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override   //계정이 사용가능한지
    public boolean isEnabled() {
        return true;
    }

    public UserEntity updateUserLogin(String refreshToken){
        this.loginDateTime = LocalDateTime.now();
        this.refreshToken = refreshToken;
        return this;
    }

    public UserEntity updateUserRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
        return this;
    }
}
