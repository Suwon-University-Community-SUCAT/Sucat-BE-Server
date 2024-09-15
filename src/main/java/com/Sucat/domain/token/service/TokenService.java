package com.Sucat.domain.token.service;

import com.Sucat.domain.token.exception.TokenException;
import com.Sucat.domain.token.model.Token;
import com.Sucat.domain.token.model.TokenResponse;
import com.Sucat.domain.token.repository.TokenRepository;
import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TokenService {
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public TokenResponse reissueAccessToken(HttpServletRequest request) {
        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> new TokenException(ErrorCode.INVALID_ACCESS_TOKEN));

        Optional<Token> tokenOpt = tokenRepository.findByAccessToken(accessToken);

        if (!tokenOpt.isPresent()) {
            throw new TokenException(ErrorCode.INVALID_TOKEN);
        }
        Token token = tokenOpt.get();

        String refreshToken = token.getRefreshToken();

        //expired check
        if (!jwtUtil.isTokenValid(refreshToken)) {
            //response status code
            throw new TokenException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.extractEmail(refreshToken);
        String role = jwtUtil.extractRole(refreshToken);

        String newAccessToken = jwtUtil.createAccessToken(email, role);

        updateAccessToken(newAccessToken, token);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    /* 리프레쉬 토큰 업데이트 또는 토큰 생성 후 저장 */
    @Transactional
    public void saveOrUpdate(String email, String refreshToken, String accessToken) {
        Token token = tokenRepository.findByAccessToken(accessToken)
                .map(t -> t.updateRefreshToken(refreshToken))
                .orElseGet(() -> new Token(email, refreshToken, accessToken));

        tokenRepository.save(token);
    }

    /* accessToken과 일치하는 token 반환 */
    public Token findByAccessTokenOrThrow(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new TokenException(ErrorCode.INVALID_ACCESS_TOKEN));
    }

    /* Access 토큰 정보 업데이트 */
    @Transactional
    public void updateAccessToken(String newAccessToken, Token token) {
        token.updateAccessToken(newAccessToken);
//        tokenRepository.save(token);

        log.info(token.getAccessToken());
    }
}
