package com.Sucat.domain.user.controller;

import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import static com.Sucat.domain.user.dto.UserDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserDetailsService userDetailsService;

    @GetMapping("/nickname/duplication")
    public ResponseEntity<ApiResponse<Object>> nicknameDuplication(@RequestBody @Valid UserNicknameRequest userNicknameRequest) {
        userService.nicknameDuplicateVerification(userNicknameRequest.nickname());
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    @GetMapping("/password")
    public ResponseEntity<ApiResponse<Object>> getCurrentUserEmail(HttpServletRequest request) {
        String email = userService.getUserInfo(request).getEmail();
        return ApiResponse.onSuccess(SuccessCode._OK, email);
    }

    @PostMapping("/password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        userService.resetPassword(passwordResetRequest);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }


    // TODO: 추후 사용자 프로필 이미지 반환 추가
    @GetMapping("/change/profile")
    public ResponseEntity<ApiResponse<Object>> getCurrentUserProfile(HttpServletRequest request) {
        UserProfileResponse userProfile = userService.getUserProfile(request);
        return ApiResponse.onSuccess(SuccessCode._OK, userProfile);
    }

    @PostMapping("/change/profile")
    public ResponseEntity<ApiResponse<Object>> updateProfile(HttpServletRequest request, @RequestBody @Valid UserProfileUpdateRequest userProfileUpdateRequest) {
        userService.updateProfile(request, userProfileUpdateRequest);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    @PostMapping("/change/password")
    public ResponseEntity<ApiResponse<Object>> changeProfile(HttpServletRequest request, @RequestBody @Valid UserPasswordUpdateRequest userPasswordUpdateRequest) {
        userService.changePassword(request, userPasswordUpdateRequest);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

}
