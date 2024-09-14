package com.Sucat.global.security.filter;

import com.Sucat.domain.user.repository.UserRepository;
import com.Sucat.global.security.CustomUserDetails;
import com.Sucat.global.security.dto.UserDTO;
import com.Sucat.global.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * OncePerRequestFilter: 모든 서블릿 컨테이너에서 요청 디스패치당 단일 실행을 보장하는 것을 목표로 하는 필터 기본 클래스
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    @Value("${admin.email}")
    private String adminEmail;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    private static final String NO_CHECK_URL = "/login";

    /**
     * 1. RefreshToken이 오는 경우 -> 유효하면 AccessToken 재발급후, 필터 진행 X
     * 2. RefreshToken은 없고 AccessToken만 있는 경우 -> 유저정보 저장 후 필터 진행, RefreshToken 재발급X
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Received request URI: " + request.getRequestURI());
        String requestURI = request.getRequestURI();

        String accessToken = jwtUtil.extractAccessToken(request).get();

        // 로그인 경로거나 토큰이 없을시 다음 필터로 넘김
        if (requestURI.equals(NO_CHECK_URL) || accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isTokenValid(accessToken);
        } catch (ExpiredJwtException e) {
            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        checkAccessTokenAndAuthentication(request, response, accessToken, filterChain);
        filterChain.doFilter(request, response);
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, String accessToken, FilterChain filterChain) throws ServletException, IOException {

        String username = jwtUtil.extractEmail(accessToken);
        String role = jwtUtil.extractRole(accessToken);
        UserDTO userDTO = new UserDTO(username, role);

        CustomUserDetails customUserDetails = new CustomUserDetails(userDTO);
        saveAuthentication(customUserDetails);
    }

    private void saveAuthentication(CustomUserDetails customUserDetails) {

        // Spring Security 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }



}
