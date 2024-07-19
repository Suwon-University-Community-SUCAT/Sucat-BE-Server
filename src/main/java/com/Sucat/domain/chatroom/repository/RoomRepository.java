package com.Sucat.domain.chatroom.repository;

import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySenderAndReceiver(User sender, User receiver);

    Optional<ChatRoom> findByRoomId(String roomId);
    Page<ChatRoom> findAllBySenderOrReceiver(Pageable pageable, User sender, User receiver);
}
