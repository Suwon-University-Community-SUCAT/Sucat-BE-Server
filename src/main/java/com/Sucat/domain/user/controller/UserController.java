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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.Sucat.domain.user.dto.UserDto.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserDetailsService userDetailsService;


    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Object>> signup(@RequestBody @Valid JoinUserRequest userRequest) {
        userService.emailDuplicateVerification(userRequest.email());
        String encodePassword = passwordEncoder.encode(userRequest.password());

        userService.join(userRequest.toEntity(encodePassword));
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    @GetMapping("/join/next2")
    public ResponseEntity<ApiResponse<Object>> next2(@RequestBody @Valid UserEmailRequest userEmailRequest) {
        userService.emailDuplicateVerification(userEmailRequest.email());
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    @GetMapping("/join/duplication")
    public ResponseEntity<ApiResponse<Object>> nicknameDuplication(@RequestBody @Valid UserNicknameRequest userNicknameRequest) {
        userService.nicknameDuplicateVerification(userNicknameRequest.nickname());
        return ApiResponse.onSuccess(SuccessCode._OK);
    }
}
