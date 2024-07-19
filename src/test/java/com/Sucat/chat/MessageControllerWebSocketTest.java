package com.Sucat.chat;

import com.Sucat.domain.chatmessage.dto.MessageDto;
import com.Sucat.domain.chatmessage.service.ChatService;
import com.Sucat.global.redis.PublishMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageControllerWebSocketTest {

    @Mock
    private ChatService chatService;

    @Mock
    private ChannelTopic topic;

    @Mock
    private RedisTemplate<String, PublishMessage> redisTemplate;

    @Autowired
    private WebSocketStompClient stompClient;

    private StompSession stompSession;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
        };
        stompSession = stompClient.connect("ws://localhost:{port}/ws", sessionHandler).get(1, TimeUnit.SECONDS);
    }

    @Test
    void testMessageMapping() throws Exception {
        // Given
        UUID uuid = UUID.randomUUID();
        String roomId = String.valueOf(uuid);
        MessageDto messageDto = new MessageDto(roomId, 3L, "Hello, World1");

        when(topic.getTopic()).thenReturn("testTopic");

        // When
        stompSession.send("/chats/messages/" + roomId, messageDto);

        // Then
        ArgumentCaptor<PublishMessage> publishMessageCaptor = ArgumentCaptor.forClass(PublishMessage.class);
        verify(redisTemplate, timeout(1000).times(1)).convertAndSend(eq("testTopic"), publishMessageCaptor.capture());
        verify(chatService, timeout(1000).times(1)).saveMessage(messageDto, roomId);

        PublishMessage capturedPublishMessage = publishMessageCaptor.getValue();
        assertEquals(roomId, capturedPublishMessage.getRoomId());
        assertEquals(messageDto.getSenderId(), capturedPublishMessage.getSenderId());
        assertEquals(messageDto.getContent(), capturedPublishMessage.getContent());
    }
}

