package com.Sucat.global.infra.email.model;


import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VerificationCode {
    private String code;
    private String email;
    private LocalDateTime createdAt;
    private Integer expirationTimeInMinutes;

    public boolean isExpired(LocalDateTime verifiedAt) {
        LocalDateTime expireAt = createdAt.plusMinutes(expirationTimeInMinutes);
        return verifiedAt.isAfter(expireAt);
    }

    public String generateCodeMessage() {
        String formattedExpiredAt = createdAt
                .plusMinutes(expirationTimeInMinutes)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return String.format(
                """
                        [Verification Code] 
                        %s
                        Expired At : %s
                                """,
                code, formattedExpiredAt
        );
    }
}
