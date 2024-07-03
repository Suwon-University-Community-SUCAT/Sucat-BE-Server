package com.Sucat.global.security.handler;

import com.Sucat.domain.token.model.RefreshToken;
import com.Sucat.domain.token.repository.RefreshTokenRepository;
import com.Sucat.domain.user.exception.UserException;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.repository.UserRepository;
import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJWTProvideHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 로그인 성공시
     * 응답 헤더에 AccessToken, RefreshToken 설정
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = extractEmail(authentication);

        User existUser = userRepository.findByEmail(email).get();

        if (existUser != null) {
            log.info("기존 유저입니다.");
            refreshTokenRepository.deleteByEmail(existUser.getEmail());
        } else {
            log.info("신규 유저입니다. 회원가입이 필요합니다.");
            throw new UserException(ErrorCode.USER_NOT_FOUND);
        }

        log.info("유저 이메일: {}", email);

        String accessToken = jwtUtil.createAccessToken(email); // AccessToken 발급
        String refreshToken = jwtUtil.createRefreshToken(email); // RefreshToken 발급

        RefreshToken newRefreshToken = RefreshToken.builder()
                .email(existUser.getEmail())
                .token(refreshToken)
                .build();

        refreshTokenRepository.save(newRefreshToken);

        jwtUtil.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 응답 헤더에 AccessToken, RefreshToken 설정

        log.info( "로그인에 성공합니다. email: {}" , email);
        log.info( "AccessToken 을 발급합니다. AccessToken: {}" ,accessToken);
        log.info( "RefreshToken 을 발급합니다. RefreshToken: {}" ,refreshToken);

        response.getWriter().write("success");
    }

    private String extractEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
