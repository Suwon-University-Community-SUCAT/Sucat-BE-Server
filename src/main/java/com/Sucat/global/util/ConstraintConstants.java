package com.Sucat.global.util;

public class ConstraintConstants {
    /**
     * User
     */
    public static final String USER_EMAIL = "회원 이메일";
    public static final String USER_PASSWORD = "회원 비밀번호";
    public static final String USER_NAME = "회원 이름";
    public static final String USER_NICKNAME = "회원 닉네임";
    public static final String USER_DEPARTMENT = "회원 학과";

    /**
     * Security
     */
    public static final String DEFAULT_LOGIN_REQUEST_URL = "/login";
    public static final String HTTP_METHOD = "POST";
    public static final String CONTENT_TYPE = "application/json";
    public static final String USERNAME_KEY="email";
    public static final String PASSWORD_KEY="password";

    /**
     * JWT
     */
    public static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    public static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    public static final String USERNAME_CLAIM = "pp8817@naver.com";
    public static final String BEARER = "Bearer ";

}
