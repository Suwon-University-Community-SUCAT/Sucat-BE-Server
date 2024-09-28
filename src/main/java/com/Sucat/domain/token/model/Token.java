package com.Sucat.domain.token.model;

import com.Sucat.global.common.dao.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "tokens")
public class Token extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "refreshToken", nullable = false, length = 500)
    private String refreshToken;

    @Column(name = "accessToken", nullable = false, length = 500)
    private String accessToken;

    public Token updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public void updateAccessToken(String newAccessToken) {
        this.accessToken = newAccessToken;
        log.info(this.accessToken);
    }

    public Token(String email, String refreshToken, String accessToken) {
        this.email = email;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}