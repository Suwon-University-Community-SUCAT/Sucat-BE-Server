package com.Sucat.domain.token.service;

import com.Sucat.domain.token.exception.TokenException;
import com.Sucat.domain.token.model.RefreshToken;
import com.Sucat.domain.token.model.TokenResponse;
import com.Sucat.domain.token.repository.RefreshTokenRepository;
import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public TokenResponse reissueAccessToken(HttpServletRequest request) {
        String refreshToken = jwtUtil.extractRefreshToken(request).orElseThrow(() -> new TokenException(ErrorCode.INVALID_REFRESH_TOKEN));
        String email = jwtUtil.extractEmail(refreshToken).orElseThrow(() -> new TokenException(ErrorCode.INVALID_REFRESH_TOKEN));
        RefreshToken existRefreshToken = refreshTokenRepository.findByEmail(email);
        String accessToken = null;

        if (!existRefreshToken.getToken().equals(refreshToken) || !jwtUtil.isTokenValid(refreshToken)) {
            log.info("Refresh Token이 일치하지 않거나, 만료되었습니다.");
            throw new TokenException(ErrorCode.INVALID_REFRESH_TOKEN);
        } else {
            accessToken = jwtUtil.createAccessToken(email);
        }

        return TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
