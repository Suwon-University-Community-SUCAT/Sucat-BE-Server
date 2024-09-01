package com.Sucat.domain.chatmessage.controller;

import com.Sucat.domain.chatmessage.dto.ChatMessageDto;
import com.Sucat.domain.chatmessage.dto.ChatMessageResponse;
import com.Sucat.domain.chatmessage.model.ChatMessage;
import com.Sucat.domain.chatmessage.service.ChatMessageService;
import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.chatroom.service.ChatRoomService;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import com.Sucat.global.common.response.PageInfo;
import com.Sucat.global.redis.PublishMessage;
import jakarta.annotation.Resource;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final ChannelTopic topic;

    @Resource(name = "chatRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    @MessageMapping("/chats/messages/{roomId}") // '/pub/chats/messages/{room-id}' 경로로 보낸 메시지를 처리
    public void message(@DestinationVariable("roomId") String roomId, ChatMessageDto chatMessageDto) {
        Long senderId = chatMessageDto.getSenderId();
        String content = chatMessageDto.getContent();

        PublishMessage publishMessage =
                new PublishMessage(roomId, senderId, content, LocalDateTime.now());
        log.info("publishMessage: {}", publishMessage.getContent());

        //Redis를 통해 메시지 전송
        redisTemplate.convertAndSend(topic.getTopic(), publishMessage);

        User user = userService.findById(senderId);
        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);

        chatMessageService.saveMessage(content, user, chatRoom);
    }


    // 채팅방 메시지 목록 가져오기
    @GetMapping("/api/v1/chats/messages/{room-id}")
    public ResponseEntity<ApiResponse<Object>> getMessages(@PathVariable("room-id") String roomId,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);

        Page<ChatMessage> messages =
                chatMessageService.getChatRoomMessages(chatRoom, page, size);
        PageInfo pageInfo = new PageInfo(page, size, (int)messages.getTotalElements(), messages.getTotalPages());

        List<ChatMessageResponse.ChatRoomMessageResponse> chatRoomMessageResponses = messages.getContent().stream().map(
                ChatMessageResponse.ChatRoomMessageResponse::of
        ).toList();

        ChatMessageResponse.ChatRoomMessageWithPageInfoResponse chatRoomMessageWithPageInfoResponse = ChatMessageResponse.ChatRoomMessageWithPageInfoResponse.of(chatRoomMessageResponses, pageInfo);

        return ApiResponse.onSuccess(SuccessCode._OK, chatRoomMessageWithPageInfoResponse);
    }

}
