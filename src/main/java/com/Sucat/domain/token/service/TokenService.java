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

import java.util.Optional;

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

        validateRefreshToken(email, refreshToken); // 토큰이 존재하는지, 일치하는지 검증

        String accessToken = jwtUtil.createAccessToken(email);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    private void validateRefreshToken(String email, String refreshToken) {

        Optional<RefreshToken> existRefreshTokenOpt = refreshTokenRepository.findByEmail(email);
        if (!existRefreshTokenOpt.isPresent()) {
            throw new TokenException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        if ((!existRefreshTokenOpt.get().getToken().equals(refreshToken))) {
            throw new TokenException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }


}
