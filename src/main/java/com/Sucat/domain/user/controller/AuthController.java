package com.Sucat.domain.user.controller;

import com.Sucat.domain.user.service.AuthService;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.Sucat.domain.user.dto.UserDto.JoinUserRequest;
import static com.Sucat.domain.user.dto.UserDto.UserTermAgree;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthService authService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> signup(
            @RequestPart(name = "request") @Valid JoinUserRequest userRequest,
            @RequestPart(name = "profileImage", required = true) MultipartFile profileImage) throws IOException {
        authService.signup(userRequest.toEntity(), profileImage);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /**
     * 약관 동의 페이지에서 회원가입 정보 이동 페이지로 이동하는 버튼
     * 모든 약관 동의 확인 정보가 없을 시 에러 발생 -> 약관 동의를 해주세요
     */
    @GetMapping("/signup/next")
    public ResponseEntity<ApiResponse<Object>> next(@RequestBody UserTermAgree userTermAgree) {
        authService.AllAgreementsAccepted(userTermAgree);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }
}
