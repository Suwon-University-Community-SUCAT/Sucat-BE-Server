package com.Sucat.global.common.constant;

public class SwaggerConstants {
    /**
     * swagger
     */
    public static final String[] SWAGGER_APPOINTED_PATHS = {
            "/**"
    };
    public static final String DEFINITION_TITLE = "Suwon University Communication Project API 명세서";
    public static final String DEFINITION_DESCRIPTION = "\uD83D\uDE80 Realtime Congestion Based Location Recommendation Service - Sucat Server의 API 명세서입니다.";
    public static final String DEFINITION_VERSION = "v1";

    public static final String SECURITY_SCHEME_NAME = "bearer-key";
    public static final String SECURITY_SCHEME = "bearer";
    public static final String SECURITY_SCHEME_BEARER_FORMAT = "JWT";
    public static final String SECURITY_SCHEME_DESCRIPTION = "JWT 토큰 키를 입력해주세요";

    /**
     * Common
     */
    public static final String PAGING = "pageable";
    public static final String PAGING_DESCRIPTION = "";

    /**
     * Home
     */
    public static final String TAG_HOME = "Home";
    public static final String TAG_HOME_DESCRIPTION = "Home API";
    public static final String HOME_SUMMARY = "메인 페이지";
    public static final String HOME_DESCRIPTION = "메인 페이지 정보를 가져옵니다.";

    /**
     *
     */

}
