package com.Sucat.domain.chatroom.service;

import com.Sucat.domain.chatroom.dto.ChatRoomDto;
import com.Sucat.domain.chatroom.exception.ChatRoomException;
import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.chatroom.repository.RoomRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.Sucat.domain.chatroom.dto.ChatRoomDto.ChatRoomListResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {
    private final RoomRepository roomRepository;

    /* 채팅방 생성 메서드 */
    @Transactional
    public Map<String, Object> createRoom(User sender, User receiver) {
        ChatRoom existingChatRoom = findExistingChatRoom(sender, receiver);

        if (existingChatRoom != null) {
            String roomId = existingChatRoom.getRoomId();
            log.info("Found existing chat room: {}", roomId);
            return createResponse(1, roomId);
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .roomId(UUID.randomUUID().toString())
                .build();
        ChatRoom savedChatRoom = roomRepository.save(chatRoom);
        String roomId = savedChatRoom.getRoomId();

        log.info("Created new chat room: {}", roomId);
        return createResponse(0, roomId);
    }

    /* 채팅방 목록 가져오기 메서드 */
    public List<ChatRoomListResponse> getChatRoomList(User user, String sortKey) {
        Sort sort = createSort(sortKey);

        List<ChatRoom> chatRooms = roomRepository.findAllBySenderOrReceiver(user, user, sort);

        return chatRooms.stream()
                .map(chatRoom -> {
                    User receiver = chatRoom.getSender().equals(user) ? chatRoom.getReceiver() : chatRoom.getSender();
                    return ChatRoomListResponse.of(chatRoom, receiver);
                })
                .toList();
    }

    /* 채팅방 열기 메서드 */
    public ChatRoomDto.RoomResponse openChatRoom(ChatRoom chatRoom, User sender) {
        User receiver = null;

        if (chatRoom.getSender().equals(sender)) {
            receiver = chatRoom.getReceiver();
        } else {
            receiver = chatRoom.getSender();
        }

        return ChatRoomDto.RoomResponse.of(chatRoom.getId(), sender, receiver);
    }

    /* Using Method */
    private ChatRoom findExistingChatRoom(User sender, User receiver) {
        return roomRepository.findBySenderAndReceiver(sender, receiver)
                .orElseGet(() -> roomRepository.findBySenderAndReceiver(receiver, sender).orElse(null));
    }

    private Map<String, Object> createResponse(int status, String roomId) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("roomId", roomId);
        return response;
    }

    public ChatRoom findByRoomId(String roomId) {
        return roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new ChatRoomException(ErrorCode.ROOM_NOT_FOUND));
    }

    private Sort createSort(String sortKey) {
        // 정렬 기준에 따른 Sort 객체 생성
        switch (sortKey) {
            case "createdAtDesc":
                return Sort.by(Sort.Direction.DESC, "createdAt");
            case "createdAtAsc":
                return Sort.by(Sort.Direction.ASC, "createdAt");
            default:
                throw new IllegalArgumentException("Invalid sort key: " + sortKey);
        }
    }
}
