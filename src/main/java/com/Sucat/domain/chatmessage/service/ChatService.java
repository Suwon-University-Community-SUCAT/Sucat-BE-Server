package com.Sucat.domain.chatmessage.service;

import com.Sucat.domain.chatmessage.dto.MessageDto;
import com.Sucat.domain.chatmessage.model.ChatMessage;
import com.Sucat.domain.chatmessage.repository.MessageRepository;
import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.chatroom.service.ChatRoomService;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final MessageRepository messageRepository;

    public void saveMessage(MessageDto dto,Long roomId) {
        User user = userService.findById(dto.getSenderId());
        ChatRoom chatRoom = chatRoomService.findById(roomId);

        ChatMessage chatMessage = ChatMessage.builder()
                .content(dto.getContent())
                .sender(user)
                .chatRoom(chatRoom)
                .build();

        messageRepository.save(chatMessage);
        log.info("메시지 저장 완료");
    }

    public Page<ChatMessage> getChatRoomMessages(Long roomId, int page, int size) {
        ChatRoom chatRoom = chatRoomService.findById(roomId);

        Pageable pageable = PageRequest.of(page-1, size, Sort.by("id").descending());
        Page<ChatMessage> messages = messageRepository.findByChatRoom(pageable, chatRoom);

        return messages;
    }
}
