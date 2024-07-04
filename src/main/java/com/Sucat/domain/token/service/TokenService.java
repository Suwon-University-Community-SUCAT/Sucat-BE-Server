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
        String refreshToken = jwtUtil.extractRefreshToken(request)
                .orElseThrow(() -> new TokenException(ErrorCode.INVALID_REFRESH_TOKEN));

        //extractEmail 과정에서 토큰의 유효성 검제
        String email = jwtUtil.extractEmail(refreshToken);
        RefreshToken existRefreshToken = refreshTokenRepository.findByEmail(email);

        if (!existRefreshToken.getToken().equals(refreshToken)) {
            log.info("Refresh Token이 일치하지 않습니다.");
            throw new TokenException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String accessToken = jwtUtil.createAccessToken(email);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }


}
