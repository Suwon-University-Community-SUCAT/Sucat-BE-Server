package com.Sucat.global.jwt.controller;


import com.Sucat.domain.token.model.TokenResponse;
import com.Sucat.domain.token.service.TokenService;
import com.Sucat.global.common.response.ApiResponse;
import com.Sucat.global.common.code.SuccessCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    // Refresh Token이 유효할시 Access Token 재발급 API
    @GetMapping("/reissue/access-token")
    public ResponseEntity<ApiResponse<Object>> reissueAccessToken(HttpServletRequest request) {
        TokenResponse accessToken = tokenService.reissueAccessToken(request);
        return ApiResponse.onSuccess(SuccessCode.CREATED_ACCESS_TOKEN, accessToken);
    }
}
