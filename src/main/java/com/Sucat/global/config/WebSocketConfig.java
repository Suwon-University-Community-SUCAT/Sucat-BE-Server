package com.Sucat.global.config;

import com.Sucat.global.security.handler.JwtHandshakeInterceptor;
import com.Sucat.global.websocket.handler.WebSockChatHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSockChatHandler webSockChatHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    // sockJS Fallback을 이용해 노출할 endpoint 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket이 handshake를 하기 위해 연결하는 endpoint
        registry.addEndpoint("/ws")
//                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Web Socket을 지원하지 않는 브라우저에서 HTTP의 Polling과 같은 방식으로 WebSocket의 요청을 수행하도록 돕는다.
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 서버 -> 클라이언트로 발행하는ㅁ ㅔ시지에 대한 endpoint 설정 : 구독
        config.enableSimpleBroker("/sub"); // 메시지 브로커의 Prefix를 등록, 클라이언트는 토픽을 구독할 시 /sub 경로로 요청해야 함

        // 클라이언트 -> 서버로 발행하는 메시지에 대한 endpoint 설정 : 구독에 대한 메시지
        config.setApplicationDestinationPrefixes("/pub");
    }
}