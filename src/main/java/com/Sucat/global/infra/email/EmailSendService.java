package com.Sucat.global.infra.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

import static com.Sucat.global.util.EmailConstants.*;

@Service
@RequiredArgsConstructor
public class EmailSendService {
    private final JavaMailSender mailSender;
    private final VerificationCodeRepository verificationCodeRepository;

    public void sendSimpleVerificationMail(String to, LocalDateTime sentAt) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(SERVER_EMAIL);
        mailMessage.setTo(to);
        mailMessage.setSubject(String.format("Email Verification For %s", to));

        VerificationCode verificationCode = generateVerificationCode(sentAt);
        verificationCodeRepository.save(verificationCode);

        String text = verificationCode.generateCodeMessage();
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }

    public void verifyCode(String code, LocalDateTime verifiedAt) {
        VerificationCode verificationCode = verificationCodeRepository.findByCode(code)
                .orElseThrow(NoSuchElementException::new);
        if (verificationCode.isExpired(verifiedAt)) {
            throw new NoSuchElementException();
        }

        verificationCodeRepository.remove(verificationCode);
    }

    private VerificationCode generateVerificationCode(LocalDateTime sentAt) {

        Random r = new Random();
        String code = "";
        for(int i = 0; i < 6; i++) {
            code += Integer.toString(r.nextInt(10));
        }

        return VerificationCode.builder()
                .code(code)
                .createAt(sentAt)
                .expirationTimeInMinutes(EXPIRATION_TIME_IN_MINUTES)
                .build();
    }


}
