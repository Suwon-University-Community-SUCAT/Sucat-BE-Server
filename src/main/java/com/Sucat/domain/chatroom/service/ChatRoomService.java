package com.Sucat.domain.chatroom.service;

import com.Sucat.domain.chatroom.dto.ChatRoomDto;
import com.Sucat.domain.chatroom.exception.ChatRoomException;
import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.chatroom.repository.RoomRepository;
import com.Sucat.domain.friendship.service.FriendShipService;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.Sucat.domain.chatroom.dto.ChatRoomDto.ChatRoomListResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {
    private final UserService userService;
    private final RoomRepository roomRepository;
    private final FriendShipService friendShipService;

    /* 채팅방 생성 메서드 */
    @Transactional
    public Map<String, Object> createRoom(String email, HttpServletRequest request) {
        User sender = userService.getUserInfo(request);
        User receiver = userService.findByEmail(email);

       friendShipService.validateFriendship(sender.getEmail(), receiver.getEmail()); // 친구 관계인지 검증


        // TODO 한 명이라도 상대방에게 채팅을 보낸다면 양쪽 모두에게 채팅방 정보가 저장되도록
        // 둘의 채팅이 있는지 확인
        Optional<ChatRoom> optionalChatRoom = roomRepository.findBySenderAndReceiver(sender, receiver);
        Optional<ChatRoom> optionalChatRoom2 = roomRepository.findBySenderAndReceiver(receiver, sender);

        ChatRoom chatRoom = null;

        int status = 1;
        if(optionalChatRoom.isPresent()) {
            chatRoom = optionalChatRoom.get();
            String roomId = chatRoom.getRoomId();
            log.info("Found existing chat room");

            Map<String, Object> response = new HashMap<>();
            response.put("status", status);
            response.put("roomId", roomId);

            return response;
        } else if (optionalChatRoom2.isPresent()) {
            chatRoom = optionalChatRoom2.get();
            String roomId = chatRoom.getRoomId();
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

            // Add the chat room to both users
//            sender.addChatRoom(chatRoom);
//            receiver.addChatRoom(chatRoom);
        }
        String setRoomId = UUID.randomUUID().toString();
        chatRoom.setRoomId(setRoomId);
        ChatRoom saveChatRoom = roomRepository.save(chatRoom);
        String roomId = saveChatRoom.getRoomId();

        // status와 roomId를 Map으로 반환
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("roomId", roomId);

        return response;
    }

    /* 채팅방 목록 가져오기 메서드 */
    public List<ChatRoomListResponse> getChatRoomList(HttpServletRequest request, String sortKey) {
        User user = userService.getUserInfo(request);
        Sort sort = createSort(sortKey);

        List<ChatRoom> chatRooms = roomRepository.findAllBySenderOrReceiver(user, user, sort);

        List<ChatRoomListResponse> chatRoomListResponses =
                chatRooms.stream().map(chatRoom -> {
            User receiver = chatRoom.getSender().equals(user) ? chatRoom.getReceiver() : chatRoom.getSender();
            return ChatRoomListResponse.of(chatRoom, receiver);
        }).toList();

        return chatRoomListResponses;
    }

    /* 채팅방 열기 메서드 */
    public ChatRoomDto.RoomResponse openChatRoom(String roomId, HttpServletRequest request) {
        User sender = userService.getUserInfo(request);
        ChatRoom chatRoom = findByRoomId(roomId);
        Long receiverId = null;

        if (chatRoom.getSender().getId().equals(sender.getId())) {
            receiverId = chatRoom.getReceiver().getId();
        } else {
            receiverId = chatRoom.getSender().getId();
        }

        User receiver = userService.findById(receiverId);

        return ChatRoomDto.RoomResponse.of(chatRoom.getId(), sender, receiver);
    }

    /* Using Method */
    public ChatRoom findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ChatRoomException(ErrorCode.ROOM_NOT_FOUND));
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
