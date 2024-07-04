package com.Sucat.global.jwt.service;

import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.repository.UserRepository;
import com.Sucat.global.util.JwtUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JwtFilterAuthenticationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;

    @Autowired
    JwtUtil jwtUtil;

    PasswordEncoder delegatingPasswordEncoder = new BCryptPasswordEncoder();


    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static String KEY_USERNAME = "email";
    private static String KEY_PASSWORD = "password";
    private static String USERNAME = "test@naver.com";
    private static String PASSWORD = "qwer1234!";
    private static String LOGIN_URL = "/login";


    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String BEARER = "Bearer";


    private ObjectMapper objectMapper = new ObjectMapper();



    private void clear(){
        em.flush();
        em.clear();
    }


    @BeforeEach
    private void init(){
        userRepository.save(User.builder()
                .email(USERNAME)
                .password(delegatingPasswordEncoder.encode(PASSWORD))
                .name("Member1")
                .nickname("NickName1")
                .build());
        clear();
    }



    private Map getUsernamePasswordMap(String username, String password){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERNAME, username);
        map.put(KEY_PASSWORD, password);
        return map;
    }


    private Map getAccessAndRefreshToken() throws Exception {

        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);

        MvcResult result = mockMvc.perform(
                        post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);
        String refreshToken = result.getResponse().getHeader(refreshHeader);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(accessHeader,accessToken);
        tokenMap.put(refreshHeader,refreshToken);

        return tokenMap;
    }



    /**
     * AccessToken : 존재하지 않음,
     * RefreshToken : 존재하지 않음
     */
    @Test
    public void Access_Refresh_모두_존재_X() throws Exception {
        //when, then
        mockMvc.perform(get(LOGIN_URL+"123"))//login이 아닌 다른 임의의 주소
                .andExpect(status().isForbidden());
    }

    /**
     * AccessToken : 유효,
     * RefreshToken : 존재하지 않음
     */
    @Test
    public void AccessToken만_보내서_인증() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);

        //when, then
        mockMvc.perform(get(LOGIN_URL+"123").header(accessHeader,BEARER+ accessToken))//login이 아닌 다른 임의의 주소
                .andExpectAll(status().isNotFound());

    }


    /**
     * AccessToken : 유효하지 않음,
     * RefreshToken : 존재하지 않음
     */
    @Test
    public void 안유효한AccessToken만_보내서_인증X_상태코드는_403() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);

        //when, then
        mockMvc.perform(get(LOGIN_URL+"123").header(accessHeader,accessToken+"1"))//login이 아닌 다른 임의의 주소
                .andExpectAll(status().isForbidden()); // 없는 주소로 보냈으므로 NotFound
    }


    /**
     * AccessToken : 존재하지 않음
     * RefreshToken : 유효
     */
    @Test
    public void 유효한RefreshToken만_보내서_AccessToken_재발급_200() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        // refreshToken이 null인지 확인
        assertThat(refreshToken).isNotNull();

        //when, then
        MvcResult result = mockMvc.perform(get("/login123").header(refreshHeader, BEARER + refreshToken))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);

        // accessToken이 null인지 확인
        assertThat(accessToken).isNotNull();

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getSubject();

        // subject가 예상한 값과 일치하는지 확인
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }



    /**
     * AccessToken : 존재하지 않음
     * RefreshToken : 유효하지 않음
     */
    @Test
    public void 안유효한RefreshToken만_보내면_403() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        mockMvc.perform(get(LOGIN_URL + "123").header(refreshHeader, refreshToken))//Bearer을 붙이지 않음
                .andExpect(status().isForbidden());

        mockMvc.perform(get(LOGIN_URL + "123").header(refreshHeader, BEARER+refreshToken+"1"))//유효하지 않은 토큰
                .andExpect(status().isForbidden());
    }



    /**
     * AccessToken : 유효
     * RefreshToken : 유효
     */
    @Test
    public void 유효한RefreshToken이랑_유효한AccessToken_같이보냈을때_AccessToken_재발급_200() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(LOGIN_URL+"123")
                        .header(refreshHeader, BEARER + refreshToken)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseAccessToken).getSubject();

        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
        assertThat(responseRefreshToken).isNull();//refreshToken은 재발급되지 않음
    }





    /**
     * AccessToken : 유효하지 않음
     * RefreshToken : 유효
     */
    @Test
    public void 유효한RefreshToken이랑_안유효한AccessToken_같이보냈을때_AccessToken_재발급_200() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(LOGIN_URL + "123")
                        .header(refreshHeader, BEARER + refreshToken)
                        .header(accessHeader, BEARER + accessToken + 1))
                .andExpect(status().isOk())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseAccessToken).getSubject();

        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
        assertThat(responseRefreshToken).isNull();//refreshToken은 재발급되지 않음
    }


    /**
     * AccessToken : 유효
     * RefreshToken : 유효하지 않음
     */
    @Test
    public void 안유효한RefreshToken이랑_유효한AccessToken_같이보냈을때_상태코드200_혹은404_RefreshToken은_AccessToken모두_재발급되지않음() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(LOGIN_URL + "123")
                        .header(refreshHeader, BEARER + refreshToken+1)
                        .header(accessHeader, BEARER + accessToken ))
                .andExpect(status().isNotFound())//없는 주소로 보냈으므로 NotFound
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);


        assertThat(responseAccessToken).isNull();//accessToken은 재발급되지 않음
        assertThat(responseRefreshToken).isNull();//refreshToken은 재발급되지 않음
    }



    /**
     * AccessToken : 유효하지 않음
     * RefreshToken : 유효하지 않음
     */
    @Test
    public void 안유효한RefreshToken이랑_안유효한AccessToken_같이보냈을때_403() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(LOGIN_URL + "123")
                        .header(refreshHeader, BEARER + refreshToken+1)
                        .header(accessHeader, BEARER + accessToken+1 ))
                .andExpect(status().isForbidden())//없는 주소로 보냈으므로 NotFound
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);


        assertThat(responseAccessToken).isNull();//accessToken은 재발급되지 않음
        assertThat(responseRefreshToken).isNull();//refreshToken은 재발급되지 않음

    }

    @Test
    public void 로그인_주소로_보내면_필터작동_X() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(post(LOGIN_URL)  //get인 경우 config에서 permitAll을 했기에 notFound
                        .header(refreshHeader, BEARER + refreshToken)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isBadRequest())
                .andReturn();

    }



}