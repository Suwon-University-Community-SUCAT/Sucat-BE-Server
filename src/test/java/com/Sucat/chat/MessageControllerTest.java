package com.Sucat.chat;

import com.Sucat.domain.chatmessage.controller.MessageController;
import com.Sucat.domain.chatmessage.dto.MessageDto;
import com.Sucat.domain.chatmessage.service.ChatService;
import com.Sucat.global.redis.PublishMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MessageControllerTest {

    @Mock
    private ChatService chatService;

    @Mock
    private ChannelTopic topic;

    @Mock
    private RedisTemplate<String, PublishMessage> redisTemplate;

    @InjectMocks
    private MessageController messageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMessage() {
        // Given
        UUID uuid = UUID.randomUUID();
        String roomId = String.valueOf(uuid);
        MessageDto messageDto = new MessageDto(roomId, 3L, "Hello, World!");

        when(topic.getTopic()).thenReturn("testTopic");

        // When
        messageController.message(roomId, messageDto);

        // Then
        ArgumentCaptor<PublishMessage> publishMessageCaptor = ArgumentCaptor.forClass(PublishMessage.class);
        verify(redisTemplate, times(1)).convertAndSend(eq("testTopic"), publishMessageCaptor.capture());
        verify(chatService, times(1)).saveMessage(messageDto, roomId);

        PublishMessage capturedPublishMessage = publishMessageCaptor.getValue();
        assertEquals(roomId, capturedPublishMessage.getRoomId());
        assertEquals(messageDto.getSenderId(), capturedPublishMessage.getSenderId());
        assertEquals(messageDto.getContent(), capturedPublishMessage.getContent());
    }
}


