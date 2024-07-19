package com.Sucat.domain.chatroom.controller;

import com.Sucat.domain.chatroom.dto.ChatRoomDto;
import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.chatroom.service.ChatRoomService;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatRoomController {
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    // 채팅방 주소 생성/가져오기
    @PostMapping("/{email}")
    public ResponseEntity<ApiResponse<Object>> getOrCreateRoom(@PathVariable(name = "email") String email, HttpServletRequest request) {
        Map<String, Object> room = chatRoomService.createRoom(email, request);
        UUID roomId = (UUID) room.get("roomId");
        int status = (int) room.get("status");


        URI location = UriComponentsBuilder.newInstance()
                .path("/api/v1/chats/{roomId}")
                .buildAndExpand(roomId)
                .toUri();

        if (status == 0) { // 채팅방 생성
            return ApiResponse.onSuccess(SuccessCode._CREATED, location);
        } else {
            return ApiResponse.onSuccess(SuccessCode._OK, location);
        }
    }

    //  채팅방 열기
    @GetMapping("/{room-id}")
    public ResponseEntity<ApiResponse<Object>> getChatRoom(@PathVariable("room-id") UUID roomId,
                                      HttpServletRequest request) {

        User sender = userService.getUserInfo(request);
        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);
        Long receiverId = null;
        if (chatRoom.getSender().getId().equals(sender.getId())) {
            receiverId = chatRoom.getReceiver().getId();
        } else {
            receiverId = chatRoom.getSender().getId();
        }

        User receiver = userService.findById(receiverId);

        ChatRoomDto.RoomResponse roomResponse = ChatRoomDto.RoomResponse.of(chatRoom.getId(), sender, receiver);

        return ApiResponse.onSuccess(SuccessCode._OK, roomResponse);

        // 채팅방을 열고 이전 채팅방 가져오기에서 응답 코드가 201이라면 이 메서드에서 끝이고, 200이라면 채팅방 메시지 가져오기 메서드 실행
    }

}
