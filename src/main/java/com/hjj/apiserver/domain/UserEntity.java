package com.hjj.apiserver.domain;

import com.hjj.apiserver.dto.UserDto;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Table(
        name = "tb_user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"providerId", "provider"})
        })
public class UserEntity implements UserDetails {


    private static final long serialVersionUID = -43358332789376827L;

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

    @Column(length = 100,  unique = true)
    private String userId;

    @Column(length = 20, unique = true)
    private String nickName;

    @Column(length = 200)
    private String userEmail;

    @Column(length = 300)
    private String userPw;

    @Column
    private String picture;

    @Column(length = 40)
    private String providerId;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private UserEntity.Provider provider;

    @Column(columnDefinition = "datetime default null")
    private LocalDateTime providerConnectDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userEntity", fetch = FetchType.LAZY)
    @Builder.Default
    private List<PurchaseEntityJava> purchaseEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.LAZY)
    @Builder.Default
    @OrderBy("loginDateTime desc")
    private List<UserLogEntity> userLogEntityList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userEntity", fetch = FetchType.LAZY)
    @Builder.Default
    private List<AccountBookUserEntityJava> accountBookUserEntityList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userEntity", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CardEntityJava> cardEntityList = new ArrayList<>();

    @Column(columnDefinition = "datetime default now()", nullable = false)
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime lastModifiedDate;

    @Column(columnDefinition = "char(1) default 'N'", nullable = false, insertable = false)
    private char deleteYn;

    @Column
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
        return String.valueOf(userNo);
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
        this.refreshToken = refreshToken;
        return this;
    }

    public UserEntity updateUserRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
        return this;
    }

    public UserEntity updateUser(UserDto userDto) {
        if(userDto.getNickName() != null)
            this.nickName = userDto.getNickName();
        if(userDto.getUserEmail() != null)
            this.userEmail = userDto.getUserEmail();
        if(userDto.getPicture() != null)
            this.picture = userDto.getPicture();
        if(userDto.getProvider() != null)
            this.provider = userDto.getProvider();
        if(userDto.getProviderId() != null)
            this.providerId = userDto.getProviderId();
        if(userDto.getProviderConnectDate() != null)
            this.providerConnectDate = userDto.getProviderConnectDate();
        return this;
    }
}
