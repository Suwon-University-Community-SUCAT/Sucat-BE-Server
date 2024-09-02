package com.Sucat.domain.chatmessage.service;

import com.Sucat.domain.chatmessage.model.ChatMessage;
import com.Sucat.domain.chatmessage.repository.ChatMessageRepository;
import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.chatroom.service.ChatRoomService;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.redis.PublishMessage;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    /* 채팅방 메시지 목록 조회 메서드 */
    public Page<ChatMessage> getChatRoomMessages(String roomId, int page, int size) {
        ChatRoom chatRoom = chatRoomService.findByRoomId(roomId);
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("id").descending());
        return chatMessageRepository.findByChatRoom(pageable, chatRoom);
    }
}
