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
public class UserEntity extends BaseEntity implements UserDetails {

    @RequiredArgsConstructor
    @Getter
    public enum Role {
        GUEST("ROLE_GUEST", "손님"),
        USER("ROLE_USER", "일반사용자"),
        ADMIN("ROLE_ADMIN", "관리자");

        private final String key;
        private final String title;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNo;

    @Column(length = 100, nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickName;

    @Column(length = 200, nullable = true)
    private String userEmail;

    @Column(length = 300, nullable = true)
    private String userPw;

    @Column(nullable = true)
    private String picture;

    @Column(nullable = true)
    private String provider;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userInfo")
    private List<PurchaseEntity> purchaseEntityList;

    @Column
    private LocalDateTime loginDateTime;

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
}
