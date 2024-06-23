package com.Sucat.global.infra.email;

import com.Sucat.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.Sucat.global.infra.email.EmailRequest.*;

@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailSendService emailService;

    /*
    인증 코드 전송
     */
    @PostMapping("/verify-email")
    public ApiResponse<String> getEmailForVerification(@RequestBody EmailForVerificationRequest request) {
        LocalDateTime requestedAt = LocalDateTime.now();
        emailService.sendSimpleVerificationMail(request.getEmail(), requestedAt);
        return ApiResponse.successWithNoContent();
    }

    /*
    이메일 인증 확인
     */
    @PostMapping("/verification-code")
    public ApiResponse<String> verificationByCode(@RequestBody VerificationCodeRequest request) {
        LocalDateTime requestedAt = LocalDateTime.now();
        emailService.verifyCode(request.getCode(), requestedAt);
        return ApiResponse.success("OK");
    }
}
