package com.Sucat.global.security.filter;

import com.Sucat.domain.token.exception.TokenException;
import com.Sucat.domain.token.model.BlacklistedToken;
import com.Sucat.domain.token.repository.BlacklistedTokenRepository;
import com.Sucat.domain.token.repository.TokenRepository;
import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@Component
public class CustomLogoutFilter extends GenericFilterBean {
    private final JwtUtil jwtUtil;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final TokenRepository tokenRepository;

    public CustomLogoutFilter(JwtUtil jwtUtil, BlacklistedTokenRepository blacklistedTokenRepository, TokenRepository tokenRepository) {
        this.jwtUtil = jwtUtil;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtUtil.extractAccessToken(request).orElseThrow(() -> new TokenException(ErrorCode.INVALID_ACCESS_TOKEN));

        if (!tokenRepository.existsByAccessToken(accessToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            if (!blacklistedTokenRepository.existsByToken(accessToken)) {
                blacklistedTokenRepository.save(new BlacklistedToken(accessToken));
            }

            // 토큰 정보 DB에서 제거
            String email = jwtUtil.extractEmail(accessToken);
            tokenRepository.deleteByEmail(email);

            response.setStatus(HttpServletResponse.SC_OK);
        }

    }
}