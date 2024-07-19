//package com.Sucat.global.jwt.service;
//
//import com.Sucat.domain.user.model.User;
//import com.Sucat.domain.user.repository.UserRepository;
//import com.Sucat.global.util.JwtUtil;
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import jakarta.persistence.EntityManager;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//
//import static com.Sucat.global.constant.ConstraintConstants.*;
//import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest
//@Transactional
//class JwtUtilTest {
//
//    @Autowired
//    JwtUtil jwtUtil;
//    @Autowired
//    UserRepository userRepository;
//    @Autowired
//    EntityManager em;
//
//    @Value("${jwt.secret}")
//    private String secret;
//    @Value("${jwt.access.header}")
//    private String accessHeader;
//    @Value("${jwt.refresh.header}")
//    private String refreshHeader;
//
//    private String email = "pp8817@naver.com";
//
//    @BeforeEach
//    public void init() {
//        User user = User.builder().email(email).password("1234567890").name("Member1").nickname("NickName1").build();
//        userRepository.save(user);
//        clear();
//    }
//
//    private void clear() {
//        em.flush();
//        em.clear();
//    }
//
//    private DecodedJWT getVerify(String token) {
//        return JWT.require(HMAC512(secret)).build().verify(token);
//    }
//
//    //AccessToken 발급 테스트
//    @Test
//    public void createAccessToken_AccessToken_발급() throws Exception {
//        //given, when
//        String accessToken = jwtUtil.createAccessToken(email);
//        DecodedJWT verify = getVerify(accessToken);
//        String subject = verify.getSubject();
//        String finaEmail = verify.getClaim(USERNAME_CLAIM).asString();
//        //then
//        assertThat(finaEmail).isEqualTo(email);
//        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
//    }
//
//    //RefreshToken 발급 테스트
//    @Test
//    public void createRefreshToken_RefreshToken_발급() throws Exception {
//        //given, when
//        String refreshToken = jwtUtil.createRefreshToken();
//        DecodedJWT verify = getVerify(refreshToken);
//        String subject = verify.getSubject();
//        String finaEmail = verify.getClaim(USERNAME_CLAIM).asString();
//
//        //then
//        assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
//        assertThat(finaEmail).isNull();
//    }
//
//    //RefreshToken 업데이트
//    @Test
//    public void updateRefreshToken_refreshToken_업데이트() throws Exception {
//        //given
//        String refreshToken = jwtUtil.createRefreshToken();
//        jwtUtil.updateRefreshToken(email, refreshToken);
//        clear();
//        Thread.sleep(3000);
//
//        //when
//        String reIssuedRefreshToken = jwtUtil.createRefreshToken();
//        jwtUtil.updateRefreshToken(email, reIssuedRefreshToken);
//        clear();
//
//        //then
//        assertThrows(Exception.class, () -> userRepository.findByRefreshToken(refreshToken).get());//
//        assertThat(userRepository.findByRefreshToken(reIssuedRefreshToken).get().getEmail()).isEqualTo(email);
//    }
//
//    //RefreshToken 제거
//    @Test
//    public void destroyRefreshToken_refreshToken_제거() throws Exception {
//        //given
//        String refreshToken = jwtUtil.createRefreshToken();
//        jwtUtil.updateRefreshToken(email, refreshToken);
//        clear();
//
//        //when
//        jwtUtil.destroyRefreshToken(email);
//        clear();
//
//        //then
//        assertThrows(Exception.class, () -> userRepository.findByRefreshToken(refreshToken).get());
//
//        User user = userRepository.findByEmail(email).get();
//        assertThat(user.getRefreshToken()).isNull();
//    }
//
//    // AccessToken, RefreshToken 헤더 설정 테스트
//    @Test
//    public void setAccessTokenHeader_AccessToken_헤더_설정() throws Exception {
//        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
//
//        String accessToken = jwtUtil.createAccessToken(email);
//        String refreshToken = jwtUtil.createRefreshToken();
//
//        //when
//        jwtUtil.setAccessTokenHeader(mockHttpServletResponse, accessToken);
//
//        //then
//        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
//
//        assertThat(headerAccessToken).isEqualTo(accessToken);
//    }
//
//    @Test
//    public void setRefreshTokenHeader_RefreshToken_헤더_설정() throws Exception {
//        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
//
//        String accessToken = jwtUtil.createAccessToken(email);
//        String refreshToken = jwtUtil.createRefreshToken();
//        //when
//        jwtUtil.setRefreshTokenHeader(mockHttpServletResponse, refreshToken);
//
//        //then
//        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);
//
//        assertThat(headerRefreshToken).isEqualTo(refreshToken);
//    }
//
//    @Test
//    public void setAccessTokenHeader_AccessTokenAndRefreshToken_헤더_설정() throws Exception {
//        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
//
//        String accessToken = jwtUtil.createAccessToken(email);
//        String refreshToken = jwtUtil.createRefreshToken();
//
//        //when
//        jwtUtil.sendAccessAndRefreshToken(mockHttpServletResponse,accessToken,refreshToken);
//
//        //then
//        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
//        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);
//
//        assertThat(headerAccessToken).isEqualTo(accessToken);
//        assertThat(headerRefreshToken).isEqualTo(refreshToken);
//    }
//
//    // 토큰 전송 테스트
//    @Test
//    public void sendToken_토큰_전송() throws Exception {
//        //given
//        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
//
//        String accessToken = jwtUtil.createAccessToken(email);
//        String refreshToken = jwtUtil.createRefreshToken();
//
//
//        //when
//        jwtUtil.sendAccessAndRefreshToken(mockHttpServletResponse,accessToken,refreshToken);
//
//
//        //then
//        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
//        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);
//
//
//
//        assertThat(headerAccessToken).isEqualTo(accessToken);
//        assertThat(headerRefreshToken).isEqualTo(refreshToken);
//
//    }
//
//    //AccessToken 추출 테스트
//    @Test
//    public void extractAccessToken_AccessToken_추출() throws Exception {
//        //given
//        String accessToken = jwtUtil.createAccessToken(email);
//        String refreshToken = jwtUtil.createRefreshToken();
//        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);
//
//        //when
//        String extractAccessToken =  jwtUtil.extractAccessToken(httpServletRequest).orElseThrow(()-> new Exception("토큰이 없습니다"));
//
//        //then
//        assertThat(extractAccessToken).isEqualTo(accessToken);
//        assertThat(getVerify(extractAccessToken).getClaim(USERNAME_CLAIM).asString()).isEqualTo(email);
//    }
//
//    //RefreshToken 추출 테스트
//    @Test
//    public void extractRefreshToken_RefreshToken_추출() throws Exception {
//        //given
//        String accessToken = jwtUtil.createAccessToken(email);
//        String refreshToken = jwtUtil.createRefreshToken();
//        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);
//
//        //when
//        String extractRefreshToken = jwtUtil.extractRefreshToken(httpServletRequest).get();
//
//        //then
//        assertThat(extractRefreshToken).isEqualTo(refreshToken);
//        assertThat(getVerify(extractRefreshToken).getSubject()).isEqualTo(REFRESH_TOKEN_SUBJECT);
//    }
//
//    //email 추출 테스트
//    @Test
//    public void extractUsername_Email_추출() throws Exception {
//        //given
//        String accessToken = jwtUtil.createAccessToken(email);
//        String refreshToken = jwtUtil.createRefreshToken();
//        HttpServletRequest httpServletRequest = setRequest(accessToken, refreshToken);
//
//        String requestAccessToken = jwtUtil.extractAccessToken(httpServletRequest).get();
//
//        //when
//        String extractEmail = jwtUtil.extractEmail(requestAccessToken).get();
//
//
//        //then
//        assertThat(extractEmail).isEqualTo(email);
//    }
//
//    // 토큰 유효성 감사
//    @Test
//    public void 토큰_유효성_검사() throws Exception {
//        //given
//        String accessToken = jwtUtil.createAccessToken(email);
//        String refreshToken = jwtUtil.createRefreshToken();
//
//        //when, then
//        assertThat(jwtUtil.isTokenValid(accessToken)).isTrue();
//        assertThat(jwtUtil.isTokenValid(refreshToken)).isTrue();
//        assertThat(jwtUtil.isTokenValid(accessToken+"d")).isFalse();
//        assertThat(jwtUtil.isTokenValid(accessToken+"d")).isFalse();
//
//    }
//
//
//
//    private HttpServletRequest setRequest(String accessToken, String refreshToken) throws IOException {
//
//        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
//        jwtUtil.sendAccessAndRefreshToken(mockHttpServletResponse,accessToken,refreshToken);
//
//        String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
//        String headerRefreshToken = mockHttpServletResponse.getHeader(refreshHeader);
//
//        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
//
//        httpServletRequest.addHeader(accessHeader, BEARER+headerAccessToken);
//        httpServletRequest.addHeader(refreshHeader, BEARER+headerRefreshToken);
//
//        return httpServletRequest;
//    }
//}