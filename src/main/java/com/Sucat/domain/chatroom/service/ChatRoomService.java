package com.Sucat.domain.chatroom.service;

import com.Sucat.domain.chatroom.exception.ChatRoomException;
import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.chatroom.repository.RoomRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {
    private final UserService userService;
    private final RoomRepository roomRepository;

    public Map<String, Object> createRoom(String email, HttpServletRequest request) {
        User sender = userService.getUserInfo(request);
        User receiver = userService.findByEmail(email);

        // TODO 한 명이라도 상대방에게 채팅을 보낸다면 양쪽 모두에게 채팅방 정보가 저장되도록
        // 둘의 채팅이 있는지 확인
        Optional<ChatRoom> optionalChatRoom = roomRepository.findBySenderAndReceiver(sender, receiver);
        Optional<ChatRoom> optionalChatRoom2 = roomRepository.findBySenderAndReceiver(receiver, sender);

        ChatRoom chatRoom = null;

        // TODO: 채팅방 roomId를 UUID로 변경

        int status = 1;
        if(optionalChatRoom.isPresent()) {
            chatRoom = optionalChatRoom.get();
            UUID roomId = chatRoom.getRoomId();
            log.info("Found existing chat room");

            Map<String, Object> response = new HashMap<>();
            response.put("status", status);
            response.put("roomId", roomId);

            return response;
        } else if (optionalChatRoom2.isPresent()) {
            chatRoom = optionalChatRoom2.get();
            UUID roomId = chatRoom.getRoomId();
            log.info("Found existing chat room");

            Map<String, Object> response = new HashMap<>();
            response.put("status", status);
            response.put("roomId", roomId);

            return response;
        } else {
            chatRoom = ChatRoom.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .build();
            log.info("Create new chat room");
            status = 0;
        }
        UUID setRoomId = UUID.randomUUID();
        chatRoom.setRoomId(setRoomId);
        ChatRoom saveChatRoom = roomRepository.save(chatRoom);
        UUID roomId = saveChatRoom.getRoomId();

        // status와 roomId를 Map으로 반환
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("roomId", roomId);

        return response;
    }

    public ChatRoom findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ChatRoomException(ErrorCode.ROOM_NOT_FOUND));
    }

    public ChatRoom findByRoomId(UUID roomId) {
        return roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new ChatRoomException(ErrorCode.ROOM_NOT_FOUND));
    }
}
