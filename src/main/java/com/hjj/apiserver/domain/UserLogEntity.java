package com.hjj.apiserver.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="tb_user_log")
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class UserLogEntity {

    public enum SignInType{
        KAKAO, NAVER, GENERAL
    }

    public enum LogType {
        INSERT, SIGNIN, DELETE, MODIFY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logNo;

    @Column
    private LocalDateTime loginDateTime;

    @Column
    @Enumerated(EnumType.STRING)
    private SignInType signInType;

    @Column
    @Enumerated(EnumType.STRING)
    private LogType logType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userEntity_userNo")
    private UserEntity userInfo;

    @Column
    private LocalDateTime createdDate;
}
