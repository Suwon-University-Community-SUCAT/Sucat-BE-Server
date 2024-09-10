package com.Sucat.domain.chatroom.controller;

import com.Sucat.domain.chatroom.service.ChatRoomService;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.annotation.CurrentUser;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.Sucat.domain.chatroom.dto.ChatRoomDto.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    /* 채팅방 주소 생성/가져오기 */
    @PostMapping("/{email}")
    public ResponseEntity<ApiResponse<Object>> getOrCreateRoom(@PathVariable(name = "email") String email, @CurrentUser User sender) {

        ChatRoomCreationResponse creationResponse = chatRoomService.getOrCreateRoom(sender, email);

        URI location = buildChatRoomUri(creationResponse.roomId());

        if (creationResponse.status() == 0) { // 채팅방 생성
            return ApiResponse.onSuccess(SuccessCode._CREATED, location);
        } else { // 이미 채팅방 존재. 채팅방 주소 반환
            return ApiResponse.onSuccess(SuccessCode._OK, location);
        }
    }

    /* 채팅방 목록 */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getChats(
            @CurrentUser User user,
            @RequestParam(name = "sortKey", defaultValue = "createdAtDesc") @Nullable final String sortKey) {
        List<ChatRoomListResponse> chatRoomListResponses = chatRoomService.getChatRoomList(user, sortKey);

        return ApiResponse.onSuccess(SuccessCode._OK, chatRoomListResponses);
    }

    private URI buildChatRoomUri(String roomId) {
        return UriComponentsBuilder.newInstance()
                .path("/api/v1/chats/{roomId}")
                .buildAndExpand(roomId)
                .toUri();
    }

}
