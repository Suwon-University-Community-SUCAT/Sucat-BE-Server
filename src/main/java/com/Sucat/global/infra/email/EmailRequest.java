package com.Sucat.global.infra.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;

public class EmailRequest {
    @Getter
    public static class EmailForVerificationRequest {
        private String email;
    }

    @Getter
    public static class VerificationCodeRequest {
        private String code;
    }
}
