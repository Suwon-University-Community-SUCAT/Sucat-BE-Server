package com.Sucat.domain.chatroom.controller;

import com.Sucat.domain.chatmessage.model.ChatMessage;
import com.Sucat.domain.chatmessage.service.ChatService;
import com.Sucat.domain.chatroom.service.ChatRoomService;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import com.Sucat.global.common.response.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.Sucat.domain.chatmessage.dto.MessageResponse.ChatRoomMessageResponse;
import static com.Sucat.domain.chatmessage.dto.MessageResponse.ChatRoomMessageWithPageInfoResponse;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatRoomController {
    private final UserService userService;
    private final ChatService chatService;
    private final ChatRoomService chatRoomService;

    // 채팅방 주소 생성/가져오기
    // TODO - 채팅방 생성/가져오기 경우는 반환 HTTP 코드를 다르게 변경 201/200
    @PostMapping("/{email}")
    public ResponseEntity<ApiResponse<Object>> getOrCreateRoom(@PathVariable(name = "email") String email, HttpServletRequest request) {
        Long roomId = chatRoomService.createRoom(email, request);

        URI location = UriComponentsBuilder.newInstance()
//                .path("/api/v1/chats/{room-id}")
                .path("/api/v1/chats/{roomId}")
                .buildAndExpand(roomId)
                .toUri();

        return ApiResponse.onSuccess(SuccessCode._OK, location);
    }

    // 채팅방 열기
    @GetMapping("/{room-id}")
    public ResponseEntity<ApiResponse<Object>> getMessages(@PathVariable("room-id") Long roomId,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           HttpServletRequest request) {
        Page<ChatMessage> messages =
                chatService.getChatRoomMessages(roomId, page, size);
        PageInfo pageInfo = new PageInfo(page, size, (int)messages.getTotalElements(), messages.getTotalPages());

        List<ChatRoomMessageResponse> chatRoomMessageResponses = messages.getContent().stream().map(
                ChatRoomMessageResponse::of
        ).toList();

        ChatRoomMessageWithPageInfoResponse chatRoomMessageWithPageInfoResponse = ChatRoomMessageWithPageInfoResponse.of(chatRoomMessageResponses, pageInfo);

        return ApiResponse.onSuccess(SuccessCode._OK, chatRoomMessageWithPageInfoResponse);
    }

}
