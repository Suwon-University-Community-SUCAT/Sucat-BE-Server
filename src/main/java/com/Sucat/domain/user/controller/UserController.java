package com.Sucat.domain.user.controller;

import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import static com.Sucat.domain.user.dto.UserDto.PasswordResetRequest;
import static com.Sucat.domain.user.dto.UserDto.UserNicknameRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

//    private final PasswordEncoder passwordEncoder;
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
    public ResponseEntity<ApiResponse<Object>> resetPassword(HttpServletRequest request, @RequestBody PasswordResetRequest passwordResetRequest) {
        User currentUser = userService.getUserInfo(request);
        userService.resetPassword(currentUser, passwordResetRequest);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }
}
