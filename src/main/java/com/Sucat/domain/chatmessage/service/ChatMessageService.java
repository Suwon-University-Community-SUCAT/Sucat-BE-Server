package com.Sucat.domain.chatmessage.service;

import com.Sucat.domain.chatmessage.model.ChatMessage;
import com.Sucat.domain.chatmessage.repository.ChatMessageRepository;
import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

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
    public Page<ChatMessage> getChatRoomMessages(ChatRoom chatRoom, int page, int size) {

        Pageable pageable = PageRequest.of(page-1, size, Sort.by("id").descending());

        return chatMessageRepository.findByChatRoom(pageable, chatRoom);
    }
}
