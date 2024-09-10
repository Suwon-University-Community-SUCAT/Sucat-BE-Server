package com.Sucat.domain.chatmessage.controller;

import com.Sucat.domain.chatmessage.dto.ChatMessageDto;
import com.Sucat.domain.chatmessage.service.ChatMessageService;
import com.Sucat.domain.chatroom.dto.ChatRoomDto;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.annotation.CurrentUser;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    // TODO 보낸 사용자 정보를 senderId로 받는 것이 아닌, 토큰 정보를 조회에서 하도록 변경
    @MessageMapping("/chats/messages/{roomId}")
    public void message(@DestinationVariable("roomId") String roomId, ChatMessageDto chatMessageDto) {
        Long senderId = chatMessageDto.getSenderId();
        String content = chatMessageDto.getContent();

        chatMessageService.handleMessage(roomId, senderId, content);
    }

    /* 채팅방 열기, 채팅방 메시지 목록 조회*/
    @GetMapping("/api/v1/chatrooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<Object>> openChatroomWithMessages(@PathVariable("roomId") String roomId,
                                                                        @RequestParam(name = "lastMessageId", required = false) Long lastMessageId,
                                                                        @RequestParam(defaultValue = "30") int size,
                                                                        @CurrentUser User user) {
        ChatRoomDto.ChatRoomInfoWithMessagesResponse chatRoomInfoWithMessagesResponse = chatMessageService.getMessagesForInfiniteScroll(roomId, lastMessageId, size, user);
        return ApiResponse.onSuccess(SuccessCode._OK, chatRoomInfoWithMessagesResponse);
    }
}
