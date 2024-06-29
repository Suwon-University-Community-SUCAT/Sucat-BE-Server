package com.Sucat.global.jwt.service;

import com.Sucat.domain.user.UserException;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.repository.UserRepository;
import com.Sucat.global.error.ErrorCode;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.Sucat.global.util.ConstraintConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
@Setter(value = AccessLevel.PRIVATE)
@Slf4j
public class JwtServiceImpl implements JwtService{

    /*jwt.yml에 설정된 값 가져오기*/
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInSeconds;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidityInSeconds;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private final UserRepository userRepository;
    private ObjectMapper objectMapper;


    @Override
    public String createAccessToken(String email) {
        return JWT.create() // JWT 생성 빌더를 초기화
                .withSubject(ACCESS_TOKEN_SUBJECT) // JWT의 Subject를 설정한다. subject는 토큰의 목적, 주제를 나타냄.
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000)) // 만료 시간 설정
                .withClaim(USERNAME_CLAIM, email) // 토큰에 사용자 이메일 정보를 클레임으로 추가
                .sign(Algorithm.HMAC512(secret)); // HMAC512 알고리즘을 사용하여, 토큰에 서명. 서명 키: secret 변수로 설정된 값
    }

    @Override
    public String createRefreshToken() {
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds * 1000))
                .sign(Algorithm.HMAC512(secret));
        // RefreshToken의 목적은 액세스 토큰의 갱신이기 때문에 클레임 포함X
    }

    @Override
    public void updateRefreshToken(String email, String refreshToken) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken), // 값이 존재한다면 refreshToken 업데이트
                        () -> new UserException(ErrorCode.USER_INQUIRY_FAILED) // 존재하지 않으면 예외 팔생
                );
    }

    @Override
    public void destroyRefreshToken(String email) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        User::destroyRefreshToken, // 값이 존재한다면 refreshToken 삭제
                        () -> new UserException(ErrorCode.USER_INQUIRY_FAILED) // 존재하지 않으면 예외 발생
                );
    }

    // HTTPServletResponse를 사용하여 클라이언트에게 AccessToken, RefreshToken을 전송하는 메서드
    @Override
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken); //AccessToken을 Response 헤더에 설정
        setRefreshTokenHeader(response, refreshToken); //RefreshToken을 Response 헤더에 설정

        Map<String, String> tokenMap = new HashMap<>(); //토큰을 저장할 HashMap 저장
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
        tokenMap.put(REFRESH_TOKEN_SUBJECT, refreshToken);
    }

    @Override
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
    }

    /*토큰 추출*/
    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader)).filter(
                accessToken -> accessToken.startsWith(BEARER) //토큰이 Bearer로 시작하는지 확인
        ).map(accessToken -> accessToken.replace(BEARER, "")); // Bearer 접두사 제거하여 순수한 토큰 문자열만 남긴다.
    }

    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader)).filter(
                refreshToken -> refreshToken.startsWith(BEARER)
        ).map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    @Override
    public Optional<String> extractEmail(String accessToken) {
        try {
            return Optional.ofNullable(
                    JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getClaim(USERNAME_CLAIM)
                            .asString());
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    @Override
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 Token입니다.", e.getMessage());
            return false;
        }
    }
}
