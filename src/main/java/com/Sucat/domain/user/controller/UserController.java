package com.Sucat.domain.user.controller;

import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/nickname/duplication")
    public ResponseEntity<ApiResponse<Object>> nicknameDuplication(@RequestParam("nickname") @NotNull String nickname) {
        userService.nicknameDuplicateVerification(nickname);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    @PostMapping("/password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        userService.resetPassword(passwordResetRequest);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    @GetMapping("/change/profile")
    public ResponseEntity<ApiResponse<Object>> getCurrentUserProfile(HttpServletRequest request) {
        UserProfileResponse userProfile = userService.getUserProfile(request);
        return ApiResponse.onSuccess(SuccessCode._OK, userProfile);
    }

    @PostMapping("/change/profile")
    public ResponseEntity<ApiResponse<Object>> updateProfile(
            HttpServletRequest request,
            @RequestPart(name = "userProfileUpdateRequest") @Valid UserProfileUpdateRequest userProfileUpdateRequest,
            @RequestPart(name = "image", required = false) MultipartFile image) throws IOException {
        userService.updateProfile(request, userProfileUpdateRequest, image);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    @PostMapping("/change/password")
    public ResponseEntity<ApiResponse<Object>> changeProfile(HttpServletRequest request, @RequestBody @Valid UserPasswordUpdateRequest userPasswordUpdateRequest) {
        userService.changePassword(request, userPasswordUpdateRequest);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    @GetMapping("/myProfile")
    public ResponseEntity<ApiResponse<Object>> myProfile(
            HttpServletRequest request
    ) {
        UserProfileResponse userProfile = userService.getUserProfile(request);

        return ApiResponse.onSuccess(SuccessCode._OK, userProfile);
    }

}
