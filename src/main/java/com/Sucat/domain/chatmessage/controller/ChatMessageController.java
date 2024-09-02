package com.Sucat.domain.chatmessage.controller;

import com.Sucat.domain.chatmessage.dto.ChatMessageDto;
import com.Sucat.domain.chatmessage.dto.ChatMessageResponse;
import com.Sucat.domain.chatmessage.model.ChatMessage;
import com.Sucat.domain.chatmessage.service.ChatMessageService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import com.Sucat.global.common.response.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    // 채팅방 메시지 목록 가져오기
    @GetMapping("/api/v1/chats/messages/{roomId}")
    public ResponseEntity<ApiResponse<Object>> getMessages(@PathVariable("roomId") String roomId,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Page<ChatMessage> messages =
                chatMessageService.getChatRoomMessages(roomId, page, size);
        PageInfo pageInfo = new PageInfo(page, size, (int)messages.getTotalElements(), messages.getTotalPages());

        List<ChatMessageResponse.ChatRoomMessageResponse> chatRoomMessageResponses = messages.getContent().stream().map(
                ChatMessageResponse.ChatRoomMessageResponse::of
        ).toList();

        ChatMessageResponse.ChatRoomMessageWithPageInfoResponse chatRoomMessageWithPageInfoResponse = ChatMessageResponse.ChatRoomMessageWithPageInfoResponse.of(chatRoomMessageResponses, pageInfo);

        return ApiResponse.onSuccess(SuccessCode._OK, chatRoomMessageWithPageInfoResponse);
    }

}
