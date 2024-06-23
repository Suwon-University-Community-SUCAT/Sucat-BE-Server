package com.Sucat.domain.user.controller;

import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.Sucat.domain.user.dto.UserDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/join")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;


    @PostMapping
    public ApiResponse<String> signup(@RequestBody @Valid JoinUserRequest userRequest) {
        userService.emailDuplicateVerification(userRequest.email());
        String encodePassword = passwordEncoder.encode(userRequest.password());

        userService.join(userRequest.toEntity(encodePassword));

        return ApiResponse.success("회원가입 성공");
    }

//    @GetMapping("/login")
//    public ApiResponse login(@RequestBody @Valid LoginUserRequest loginUserRequest) {
//        User user = userService.findByEmail(loginUserRequest.email());
//        user
//    }

    @GetMapping("/next2")
    public ApiResponse<String> next2(@RequestBody @Valid UserEmailRequest userEmailRequest) {
        userService.emailDuplicateVerification(userEmailRequest.email());
        return ApiResponse.successWithNoContent();
    }

    @GetMapping("/duplication")
    public ApiResponse nicknameDuplication(@RequestBody @Valid UserNicknameRequest userNicknameRequest) {
        userService.nicknameDuplicateVerification(userNicknameRequest.nickname());
        return ApiResponse.successWithNoContent();
    }
}
