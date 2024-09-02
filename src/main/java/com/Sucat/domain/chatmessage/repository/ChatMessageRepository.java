package com.Sucat.domain.chatmessage.repository;

import com.Sucat.domain.chatmessage.model.ChatMessage;
import com.Sucat.domain.chatroom.model.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByChatRoom(Pageable pageable, ChatRoom chatRoom);

}
