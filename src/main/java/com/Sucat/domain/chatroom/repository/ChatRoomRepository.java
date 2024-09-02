package com.Sucat.domain.chatroom.repository;

import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.user.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomId(String roomId);

    Optional<ChatRoom> findBySenderAndReceiver(User sender, User receiver);

    List<ChatRoom> findAllBySenderOrReceiver(User sender, User receiver, Sort sort);
}
