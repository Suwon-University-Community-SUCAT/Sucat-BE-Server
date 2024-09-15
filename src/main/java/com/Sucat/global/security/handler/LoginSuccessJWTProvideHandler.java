package com.Sucat.global.security.handler;

import com.Sucat.domain.token.model.Token;
import com.Sucat.domain.token.repository.TokenRepository;
import com.Sucat.domain.user.exception.UserException;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.model.UserRole;
import com.Sucat.domain.user.repository.UserRepository;
import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.security.CustomUserDetails;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJWTProvideHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    /**
     * 로그인 성공시
     * 응답 헤더에 AccessToken, RefreshToken 설정
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String email = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        User existUser = userRepository.findByEmail(email).get();

        if (existUser != null) {
            log.info("기존 유저입니다.");
            tokenRepository.deleteByEmail(existUser.getEmail());
        } else {
            log.info("신규 유저입니다. 회원가입이 필요합니다.");
            throw new UserException(ErrorCode.USER_NOT_FOUND);
        }

        if (existUser.getRole() == UserRole.ADMIN) {
            String accessToken = jwtUtil.createAdminAccessToken(email, String.valueOf(role));
            log.info("토큰 발급: {}", accessToken);
            jwtUtil.sendAccessToken(response, accessToken);
        } else {
            String accessToken = jwtUtil.createAccessToken(email, String.valueOf(role)); // AccessToken 발급
            String refreshToken = jwtUtil.createRefreshToken(email, String.valueOf(role)); // RefreshToken 발급

            Token token = Token.builder()
                    .email(existUser.getEmail())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            tokenRepository.save(token);

            jwtUtil.sendAccessToken(response, accessToken); // 응답 헤더에 AccessToken, RefreshToken 설정
        }

        response.getWriter().write("success");
    }

}
