package com.Sucat.global.infra.email.service;

import com.Sucat.global.infra.email.exception.EmailException;
import com.Sucat.global.infra.email.model.VerificationCode;
import com.Sucat.global.infra.email.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

import static com.Sucat.global.common.code.ErrorCode.*;
import static com.Sucat.global.common.constant.EmailConstants.EXPIRATION_TIME_IN_MINUTES;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSendService {

    @Value("${spring.mail.host}")
    private String host;
    private final JavaMailSender mailSender;
    private final VerificationCodeRepository verificationCodeRepository;

    public void sendSimpleVerificationMail(String to, LocalDateTime sentAt) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(host);
        mailMessage.setTo(to);
        mailMessage.setSubject(String.format("Email Verification For %s", to));

        VerificationCode verificationCode = generateVerificationCode(sentAt, to);
        verificationCodeRepository.save(verificationCode);

        String text = verificationCode.generateCodeMessage();
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }

    public void verifyCode(String email, String code,LocalDateTime verifiedAt) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email)
                .orElseThrow(() -> new EmailException(INVALID_VERIFICATION_EMAIL));

        if (verificationCode.isExpired(verifiedAt)) {
            throw new EmailException(VERIFICATION_CODE_EXPIRED);
        }

        if (!verificationCode.getCode().equals(code)) {
            throw new EmailException(INVALID_VERIFICATION_CODE);
        }

        verificationCodeRepository.remove(verificationCode);
    }

    private VerificationCode generateVerificationCode(LocalDateTime sentAt, String to) {

        Random r = new Random();
        String code = "";
        for(int i = 0; i < 6; i++) {
            code += Integer.toString(r.nextInt(10));
        }

        log.info("이메일 인증 코드 발급: {}", code);
        return VerificationCode.builder()
                .code(code)
                .email(to)
                .createAt(sentAt)
                .expirationTimeInMinutes(EXPIRATION_TIME_IN_MINUTES)
                .build();
    }


}
