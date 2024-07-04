package com.Sucat.domain.user.controller;

import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.Sucat.domain.user.dto.UserDto.UserNicknameRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserDetailsService userDetailsService;

    @GetMapping("/nickname/duplication")
    public ResponseEntity<ApiResponse<Object>> nicknameDuplication(@RequestBody @Valid UserNicknameRequest userNicknameRequest) {
        userService.nicknameDuplicateVerification(userNicknameRequest.nickname());
        return ApiResponse.onSuccess(SuccessCode._OK);
    }
}
