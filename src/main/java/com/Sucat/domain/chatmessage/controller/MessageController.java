package com.Sucat.domain.chatmessage.controller;

import com.Sucat.domain.chatmessage.dto.MessageDto;
import com.Sucat.domain.chatmessage.dto.MessageResponse;
import com.Sucat.domain.chatmessage.model.ChatMessage;
import com.Sucat.domain.chatmessage.service.ChatService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import com.Sucat.global.common.response.PageInfo;
import com.Sucat.global.redis.PublishMessage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

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


    // 채팅방 메시지 목록 가져오기
    @GetMapping("/api/v1/chats/messages/{room-id}")
    public ResponseEntity<ApiResponse<Object>> getMessages(@PathVariable("room-id") Long roomId,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           HttpServletRequest request) {
        Page<ChatMessage> messages =
                chatService.getChatRoomMessages(roomId, page, size);
        PageInfo pageInfo = new PageInfo(page, size, (int)messages.getTotalElements(), messages.getTotalPages());

        List<MessageResponse.ChatRoomMessageResponse> chatRoomMessageResponses = messages.getContent().stream().map(
                MessageResponse.ChatRoomMessageResponse::of
        ).toList();

        MessageResponse.ChatRoomMessageWithPageInfoResponse chatRoomMessageWithPageInfoResponse = MessageResponse.ChatRoomMessageWithPageInfoResponse.of(chatRoomMessageResponses, pageInfo);

        return ApiResponse.onSuccess(SuccessCode._OK, chatRoomMessageWithPageInfoResponse);
    }

}
