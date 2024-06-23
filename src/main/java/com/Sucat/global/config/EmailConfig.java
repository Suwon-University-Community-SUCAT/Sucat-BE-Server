package com.Sucat.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String starttls;

    @Value("${spring.mail.protocol}")
    private String protocol;

    @Bean
    public JavaMailSender mailSender() {//JAVA MAILSENDER 인터페이스를 구현한 객체를 빈으로 등록하기 위함.

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl(); //JavaMailSender 의 구현체를 생성
        mailSender.setHost(host); // 속성을 추가, 이메일 전송에 사용할 SMTP 서버 호스트를 설정
        mailSender.setPort(port); // 587로 포트를 지정
        mailSender.setUsername(username); //구글 계정
        mailSender.setPassword(password); //구글 앱 비밀번호

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", protocol); //프로토콜로 smtp 사용
        properties.put("mail.smtp.auth", auth); //smtp 서버에 인증이 필요
        properties.put("mail.smtp.starttls.enable", starttls); //STARTTLS(TLS를 시작하는 명령)를 사용하여 암호화된 통신을 활성화

        return mailSender; //빈으로 등록.
    }
}
