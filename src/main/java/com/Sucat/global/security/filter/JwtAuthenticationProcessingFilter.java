package com.Sucat.global.security.filter;

import com.Sucat.global.security.CustomUserDetails;
import com.Sucat.global.security.dto.UserDTO;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * OncePerRequestFilter: 모든 서블릿 컨테이너에서 요청 디스패치당 단일 실행을 보장하는 것을 목표로 하는 필터 기본 클래스
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    /**
     * 1. RefreshToken이 오는 경우 -> 유효하면 AccessToken 재발급후, 필터 진행 X
     * 2. RefreshToken은 없고 AccessToken만 있는 경우 -> 유저정보 저장 후 필터 진행, RefreshToken 재발급X
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("request URl: {}", request.getRequestURI());
        if (isExemptUrl(request.getRequestURI())|| request.getRequestURI().equals("/")) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> accessTokenOpt = jwtUtil.extractAccessToken(request);

        if (accessTokenOpt.isEmpty()) {
            setUnauthorized(response);
            return;
        }

        String accessToken = accessTokenOpt.get();

        if (!jwtUtil.isTokenValid(accessToken)) {
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");
            setUnauthorized(response);
        } else {
            checkAccessTokenAndAuthentication(request, response, accessToken, filterChain);
            filterChain.doFilter(request, response);
        }
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, String accessToken, FilterChain filterChain) throws ServletException, IOException {

        String email = jwtUtil.extractEmail(accessToken);
        String role = jwtUtil.extractRole(accessToken);
        UserDTO userDTO = new UserDTO(email, role);

        CustomUserDetails customUserDetails = new CustomUserDetails(userDTO);
        saveAuthentication(customUserDetails);
    }

    private void saveAuthentication(CustomUserDetails customUserDetails) {

        // Spring Security 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    // 인증 실패 처리
    private void setUnauthorized(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    // 특정 URL에 대한 토큰 체크 면제
    private boolean isExemptUrl(String requestURI) {
        List<String> exemptUrls = exemptUrls();
        return exemptUrls.stream().anyMatch(requestURI::startsWith);
    }

    private List<String> exemptUrls() {
        return Arrays.asList(
                "/css/**",         // CSS 파일들
                "/images/**",      // 이미지 파일들
                "/js/**",          // 자바스크립트 파일들
                "/favicon.*",      // 파비콘 파일
                "/*/icon-*",       // 아이콘 파일
                "/error",          // 에러 페이지
                "/error/**",       // 에러 관련 경로
                "/redis/**",       // Redis 관련 경로
                "/stomp",          // STOMP 관련 경로
                "/stomp/**",       // STOMP 관련 하위 경로
                "/api/v1/users/signup/**",  // 회원가입 관련 경로
                "/api/v1/users/signup",
                "/login",          // 로그인 경로
                "/api/v1/users/password",  // 비밀번호 관련 경로
                "/api/v1/reissue/accessToken", // Access Token 재발급 경로
                "/api/v1/users/nickname/duplication",  // 닉네임 중복 확인
                "/api/v1/verification/**",  // 인증 관련 경로
                "/notification/**",  // 알림 관련 경로
                "/ws/**",            // 웹소켓 관련 경로
                "/sub/**",           // 웹소켓 구독 경로
                "/pub/**",           // 웹소켓 발행 경로
                "/chats/**",         // 채팅 관련 경로
                "/healthcheck"       // 헬스체크 경로
        );
    }


}
