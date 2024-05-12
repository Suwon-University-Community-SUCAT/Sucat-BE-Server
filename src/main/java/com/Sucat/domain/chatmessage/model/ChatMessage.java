package com.Sucat.domain.chatmessage.model;

import com.Sucat.domain.chatroom.model.ChatRoom;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "chat_massage_id")
    private Long id;

    private String content;

    @NotNull
    private Long senderId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;
}
