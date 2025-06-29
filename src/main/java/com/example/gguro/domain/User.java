package com.example.gguro.domain;

import com.example.gguro.domain.common.BaseEntity;
import com.example.gguro.domain.enums.SocialType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 아이디
    @Column(name = "username", nullable = true, length = 20)
    private String username;

    // 비밀번호
    @Column(name = "password", nullable = true, length = 100)
    private String password;

    // 보호자 이름
    @Column(name = "nickname", nullable = true, length = 20)
    private String nickname;

    // 소셜 로그인 여부
    @Column(name = "is_social_login", nullable = false)
    private Boolean isSocialLogin;

    // 이메일 (소셜 로그인 식별)
    @Column(name = "email", nullable = true)
    private String email;

    // 애플 로그인 시 제공하는 고유 식별자 (sub : 유저의 애플 id 고유 id)
    @Column(name = "oauth_id", nullable = true, unique = true)
    private String oauthId;

    // 소셜 로그인 타입 -> ENUM으로 수정
    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_type", nullable = false)
    private SocialType oauthType;
}
