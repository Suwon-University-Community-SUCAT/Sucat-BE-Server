package com.Sucat.domain.chatmessage.dto;

import com.Sucat.domain.chatmessage.model.ChatMessage;
import com.Sucat.global.common.response.PageInfo;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class MessageResponse {
    /**
     * Response
     */
    @Builder
    public record ChatRoomMessageResponse(
            Long messageId,
            String senderEmail,
            String senderName,
            String content,
            LocalDateTime sendTime
    ) {
        public static ChatRoomMessageResponse of(ChatMessage chatMessage) {
            return ChatRoomMessageResponse.builder()
                    .messageId(chatMessage.getId())
                    .senderEmail(chatMessage.getSender().getEmail())
                    .senderName(chatMessage.getSender().getName())
                    .content(chatMessage.getContent())
                    .sendTime(chatMessage.getCreatedAt())
                    .build();
        }
    }

    @Builder
    public record ChatRoomMessageWithPageInfoResponse(
            List<ChatRoomMessageResponse> chatRoomMessageResponses,
            PageInfo pageInfo
    ) {
        public static ChatRoomMessageWithPageInfoResponse of(List<ChatRoomMessageResponse> chatRoomMessageResponses, PageInfo pageInfo) {
            return ChatRoomMessageWithPageInfoResponse.builder()
                    .chatRoomMessageResponses(chatRoomMessageResponses)
                    .pageInfo(pageInfo)
                    .build();
        }
    }

}
