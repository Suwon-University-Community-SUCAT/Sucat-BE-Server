package com.Sucat.domain.chatroom.controller;

import com.Sucat.domain.chatroom.service.ChatRoomService;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatRoomController {
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    // 채팅방 주소 가져오기
    @PostMapping("/{email}")
    public ResponseEntity<ApiResponse<Object>> getOrCreateRoom(@PathVariable(name = "email") String email, HttpServletRequest request) {
        Long roomId = chatRoomService.createRoom(email, request);

        URI location = UriComponentsBuilder.newInstance()
                .path("/api/v1/chats/{room-id}")
                .buildAndExpand(roomId)
                .toUri();

        return ApiResponse.onSuccess(SuccessCode._OK, location);
    }

}
