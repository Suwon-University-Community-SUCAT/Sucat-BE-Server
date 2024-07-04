package com.Sucat.global.common.constant;

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
     * Password
     */
    // 비밀번호 정책 관련 상수
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 20;
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?]).{8,20}$";



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
    public static final String USERNAME_CLAIM = "email";
    public static final String BEARER = "Bearer";


}
