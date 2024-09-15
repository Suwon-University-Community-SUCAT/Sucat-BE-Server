package com.Sucat.domain.user.controller;

import com.Sucat.domain.user.service.AuthService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.Sucat.domain.user.dto.UserDto.JoinUserRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Object>> signup(
            @RequestPart(name = "request") @Valid JoinUserRequest userRequest,
            @RequestPart(name = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        authService.signup(userRequest.toEntity(), profileImage);
        return ApiResponse.onSuccess(SuccessCode._CREATED);
    }

    /**
     * 약관 동의 페이지에서 회원가입 정보 이동 페이지로 이동하는 버튼
     * 모든 약관 동의 확인 정보가 없을 시 에러 발생 -> 약관 동의를 해주세요
     */
    @GetMapping("/signup/next")
    public ResponseEntity<ApiResponse<Object>> next(
            @RequestParam("term1") int term1,
            @RequestParam("term2") int term2,
            @RequestParam("term3") int term3,
            @RequestParam("term4") int term4
            ) {
        authService.AllAgreementsAccepted(term1, term2, term3, term4);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }
}
