package com.Sucat.domain.chatroom.service;

import com.Sucat.domain.chatroom.exception.ChatRoomException;
import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.chatroom.repository.ChatRoomRepository;
import com.Sucat.domain.friendship.service.FriendShipService;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.Sucat.domain.chatroom.dto.ChatRoomDto.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;
    private final FriendShipService friendShipService;

    /* 채팅방 생성 메서드 */
    @Transactional
    public ChatRoomCreationResponse getOrCreateRoom(User sender, String receiverEmail) {
        User receiver = userService.findByEmail(receiverEmail);

        friendShipService.validateFriendship(sender.getEmail(), receiverEmail);

        ChatRoom existingChatRoom = findExistingChatRoom(sender, receiver);
        if (existingChatRoom != null) {
            String roomId = existingChatRoom.getRoomId();
            log.info("Found existing chat room: {}", roomId);
            return new ChatRoomCreationResponse(1, roomId);
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .roomId(UUID.randomUUID().toString())
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        String roomId = savedChatRoom.getRoomId();

        log.info("Created new chat room: {}", roomId);
        return new ChatRoomCreationResponse(0, roomId);
    }

    /* 채팅방 목록 가져오기 메서드 */
    public List<ChatRoomListResponse> getChatRoomList(User user, String sortKey) {
        Sort sort = createSort(sortKey);

        List<ChatRoom> chatRooms = chatRoomRepository.findAllBySenderOrReceiver(user, user, sort);

        return chatRooms.stream()
                .map(chatRoom -> {
                    User receiver = chatRoom.getSender().equals(user) ? chatRoom.getReceiver() : chatRoom.getSender();
                    return ChatRoomListResponse.of(chatRoom, receiver);
                })
                .toList();
    }

    /* Using Method */
    private ChatRoom findExistingChatRoom(User sender, User receiver) {
        return chatRoomRepository.findBySenderAndReceiver(sender, receiver)
                .orElseGet(() -> chatRoomRepository.findBySenderAndReceiver(receiver, sender).orElse(null));
    }

    public ChatRoom findByRoomId(String roomId) {
        return chatRoomRepository.findByRoomId(roomId)
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
