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

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {
    private final UserService userService;
    private final RoomRepository roomRepository;

    public Long createRoom(String email, HttpServletRequest request) {
        User sender = userService.getUserInfo(request);
        User receiver = userService.findByEmail(email);

        // TODO 한 명이라도 상대방에게 채팅을 보낸다면 양쪽 모두에게 채팅방 정보가 저장되도록
        // 둘의 채팅이 있는지 확인
        Optional<ChatRoom> optionalChatRoom = roomRepository.findBySenderAndReceiver(sender, receiver);
        Optional<ChatRoom> optionalChatRoom2 = roomRepository.findBySenderAndReceiver(receiver, sender);

        ChatRoom chatRoom = null;

        if(optionalChatRoom.isPresent()) {
            chatRoom = optionalChatRoom.get();
            log.info("find chat room");
            return chatRoom.getId();
        } else if (optionalChatRoom2.isPresent()) {
            chatRoom = optionalChatRoom2.get();
            log.info("find chat room");
            return chatRoom.getId();
        } else {
            chatRoom = ChatRoom.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .build();
            log.info("create chat room");
        }

        ChatRoom saveChatRoom = roomRepository.save(chatRoom);

        return saveChatRoom.getId();
    }

    public ChatRoom findById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomException(ErrorCode.ROOM_NOT_FOUND));
    }
}
