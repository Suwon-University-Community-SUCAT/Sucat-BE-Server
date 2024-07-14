package com.Sucat.global.security.filter;

import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.repository.UserRepository;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * OncePerRequestFilter: 모든 서블릿 컨테이너에서 요청 디스패치당 단일 실행을 보장하는 것을 목표로 하는 필터 기본 클래스
 */
@RequiredArgsConstructor
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

        if (requestURI.equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = extractEmailFromRequest(request);

        // Admin 계정인 경우 인증 생략
        if (email != null && isAdminEmail(email)) {
            User adminUser = userRepository.findByEmail(email).orElse(null);
            if (adminUser != null) {
                saveAuthentication(adminUser);
                filterChain.doFilter(request, response);
                return;
            }
        }

        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        jwtUtil.extractAccessToken(request).filter(jwtUtil::isTokenValid).ifPresent(
                accessToken -> {
                    String email = jwtUtil.extractEmail(accessToken);
                    userRepository.findByEmail(email).ifPresent(
                            this::saveAuthentication
                    );
                }
        );

        filterChain.doFilter(request,response);
    }

    private void saveAuthentication(User user) {
        UserDetails userDetails = createUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    /**
     * Admin 계정으로 로그인하는 경우에는 헤더에 이메일 정보 포함
     * 프론트 단에서 현재 로그인을 시도하는 Email이
     */
    private String extractEmailFromRequest(HttpServletRequest request) {
        return request.getHeader("X-Admin-Email");
    }

    private boolean isAdminEmail(String email) {
        return email.equals(adminEmail);
    }
}
