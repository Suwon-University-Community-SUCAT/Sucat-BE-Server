package com.Sucat.global.infra.email.api;

import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import com.Sucat.global.infra.email.service.EmailSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.Sucat.global.infra.email.dto.EmailRequest.EmailForVerificationRequest;
import static com.Sucat.global.infra.email.dto.EmailRequest.VerificationCodeRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class EmailController {
    private final EmailSendService emailService;

    /*
    인증 코드 전송
     */
    @PostMapping("/verification/email")
    public ResponseEntity<ApiResponse<Object>> getEmailForVerification(@RequestBody EmailForVerificationRequest request) {
        LocalDateTime requestedAt = LocalDateTime.now();
        emailService.sendSimpleVerificationMail(request.getEmail(), requestedAt);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /*
    이메일 인증 확인
     */
    @PostMapping("/verification/code")
    public ResponseEntity<ApiResponse<Object>> verificationByCode(@RequestBody VerificationCodeRequest request) {
        LocalDateTime requestedAt = LocalDateTime.now();
        emailService.verifyCode(request.getEmail(), request.getCode(), requestedAt);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }
}
