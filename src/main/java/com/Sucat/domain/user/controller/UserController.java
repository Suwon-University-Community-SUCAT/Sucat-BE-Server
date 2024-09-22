package com.Sucat.domain.user.controller;

import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.annotation.CurrentUser;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.Sucat.domain.user.dto.UserDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserDetailsService userDetailsService;

    /* 닉네임 중복 검사 */
    @GetMapping("/nickname/duplication")
    public ResponseEntity<ApiResponse<Object>> nicknameDuplication(@RequestParam("nickname") @NotNull String nickname) {
        userService.nicknameDuplicateVerification(nickname);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 비밀번호 찾기/초기화 */
    @PostMapping("/password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        userService.resetPassword(passwordResetRequest);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 프로필 변경 - 기존 정보 가져오기 */
    @GetMapping("/change/profile")
    public ResponseEntity<ApiResponse<Object>> getCurrentUserProfile(@CurrentUser User user) {
        UserProfileResponse userProfile = userService.getUserProfile(user);
        return ApiResponse.onSuccess(SuccessCode._OK, userProfile);
    }

    /* 프로필 변경 */
    @PatchMapping("/change/profile")
    public ResponseEntity<ApiResponse<Object>> updateProfile(
            @CurrentUser User user,
            @RequestPart(name = "userProfileUpdateRequest") @Valid UserProfileUpdateRequest userProfileUpdateRequest,
            @RequestPart(name = "image", required = false) MultipartFile image) throws IOException {
        userService.updateProfile(user, userProfileUpdateRequest, image);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    @PostMapping("/change/password")
    public ResponseEntity<ApiResponse<Object>> changeProfile(@CurrentUser User user, @RequestBody @Valid UserPasswordUpdateRequest userPasswordUpdateRequest) {
        userService.changePassword(user, userPasswordUpdateRequest);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    @GetMapping("/myProfile")
    public ResponseEntity<ApiResponse<Object>> myProfile(
            @CurrentUser User user
    ) {
        UserProfileResponse userProfile = userService.getUserProfile(user);

        return ApiResponse.onSuccess(SuccessCode._OK, userProfile);
    }

}
