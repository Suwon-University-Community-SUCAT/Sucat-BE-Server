package com.Sucat.global.jwt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
/*
Bearer 인증 방식 사용: AccessToken을 HTTP 요청의 Authorization 헤더에 포함하여 전송
 */
public class JwtToken {
    private String grantType; //JWT에 대한 인증 타입
    private String accessToken;
    private String refreshToken;
}
