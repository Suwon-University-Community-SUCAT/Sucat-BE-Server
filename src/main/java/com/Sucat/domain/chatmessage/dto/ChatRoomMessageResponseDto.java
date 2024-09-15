package com.Sucat.domain.chatmessage.dto;

import com.Sucat.domain.chatmessage.model.ChatMessage;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatRoomMessageResponseDto {
    private Long messageId;
    private String content;
    private Long senderId;
    private LocalDateTime sendTime;

    @Builder
    public ChatRoomMessageResponseDto(Long messageId, String content, Long senderId, LocalDateTime sendTime) {
        this.messageId = messageId;
        this.content = content;
        this.senderId = senderId;
        this.sendTime = sendTime;
    }

    @Builder
    public static ChatRoomMessageResponseDto of(ChatMessage chatMessage) {
        return ChatRoomMessageResponseDto.builder()
                .messageId(chatMessage.getId())
                .content(chatMessage.getContent())
                .senderId(chatMessage.getSender().getId())
                .sendTime(chatMessage.getCreatedAt())
                .build();
    }
}
