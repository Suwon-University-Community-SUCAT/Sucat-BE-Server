package com.Sucat.domain.chatmessage.controller;

import com.Sucat.domain.chatmessage.dto.MessageDto;
import com.Sucat.domain.chatmessage.service.ChatService;
import com.Sucat.global.redis.PublishMessage;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final ChatService chatService;
    private final ChannelTopic topic;

    @Resource(name = "chatRedisTemplate")
    private final RedisTemplate redisTemplate;

    @MessageMapping("/chats/messages/{room-id}") // '/pub/chats/messages/{room-id}' 경로로 보낸 메시지를 처리
    public void message(@DestinationVariable("room-id") Long roomId, MessageDto messageDto) {
        PublishMessage publishMessage =
                new PublishMessage(messageDto.getRoomId(), messageDto.getSenderId(), messageDto.getContent(), LocalDateTime.now());
        log.info("publishMessage: {}", publishMessage.getContent());

        //Redis를 통해 메시지 전송
        redisTemplate.convertAndSend(topic.getTopic(), publishMessage);

        chatService.saveMessage(messageDto, roomId);
    }

}
