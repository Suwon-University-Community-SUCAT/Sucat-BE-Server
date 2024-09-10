package com.Sucat.domain.chatmessage.service;

import com.Sucat.domain.chatmessage.dto.ChatRoomMessageResponseDto;
import com.Sucat.domain.chatmessage.model.ChatMessage;
import com.Sucat.domain.chatmessage.repository.ChatMessageRepository;
import com.Sucat.domain.chatroom.dto.ChatRoomDto;
import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.chatroom.service.ChatRoomService;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.error.exception.BusinessException;
import com.Sucat.global.redis.PublishMessage;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChannelTopic topic;
    @Resource(name = "chatRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    /* 메시지 전송 메서드 */
    @Transactional
    public void handleMessage(String roomId, Long senderId, String content) {
        User user = userService.findById(senderId);
        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);

        // Redis를 통해 메시지 전송
        PublishMessage publishMessage = new PublishMessage(roomId, senderId, content, LocalDateTime.now());
        redisTemplate.convertAndSend(topic.getTopic(), publishMessage);

        saveMessage(content, user, chatRoom);
    }

    /* 메시지 저장 메서드 */
    @Transactional
    public void saveMessage(String content, User user, ChatRoom chatRoom) {

        ChatMessage chatMessage = ChatMessage.builder()
                .content(content)
                .sender(user)
                .chatRoom(chatRoom)
                .build();

        chatMessageRepository.save(chatMessage);
        log.info("메시지 저장 완료");
    }

    /* 채팅방 열기, 채팅방 메시지 목록 조회 메서드 */
    public ChatRoomDto.ChatRoomInfoWithMessagesResponse getMessagesForInfiniteScroll(String roomId, Long lastMessageId, int size, User currentUser) {
        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);
        User receiver = findReceiver(currentUser, chatRoom);

        Long messageId = Optional.ofNullable(lastMessageId).orElse(Long.MAX_VALUE);
        PageRequest pageable = PageRequest.of(0, size, Sort.by("id").descending());
        Page<ChatMessage> pagedMessages = chatMessageRepository.findMessagesByRoomIdBeforeMessageId(roomId, messageId, pageable);

        // 빈 채팅방일 경우 빈 리스트를 반환
        if (pagedMessages.isEmpty()) {
            return ChatRoomDto.ChatRoomInfoWithMessagesResponse.of(chatRoom.getId(), currentUser, receiver, Collections.emptyList());
        }

        List<ChatRoomMessageResponseDto> chatRoomMessageResponseDtoList = pagedMessages.stream()
                .map(ChatRoomMessageResponseDto::of)
                .toList();

        return ChatRoomDto.ChatRoomInfoWithMessagesResponse.of(chatRoom.getId(), currentUser, receiver, chatRoomMessageResponseDtoList);
    }

    /* Using Method */
    private static User findReceiver(User currentUser, ChatRoom chatRoom) {
        User receiver = null;
        if (chatRoom.getSender().equals(currentUser)) {
            return chatRoom.getReceiver();
        } else if (chatRoom.getReceiver().equals(currentUser)) {
            return chatRoom.getSender();
        }
        throw new BusinessException(ErrorCode._FORBIDDEN);
    }
}
