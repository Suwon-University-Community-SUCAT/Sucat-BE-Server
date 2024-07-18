package com.Sucat.domain.chatmessage.dto;

import com.Sucat.domain.chatmessage.model.ChatMessage;
import com.Sucat.domain.user.dto.UserDto;
import com.Sucat.global.common.response.PageInfo;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class MessageResponse {
    /**
     * Response
     */
    @Builder
    public record ChatRoomMessageResponse( // 채팅방 속 하나의 메시지
            Long messageId,
            UserDto.ResponseOnlyUserNameWithId sender,
            String content,
            LocalDateTime sendTime
    ) {
        public static ChatRoomMessageResponse of(ChatMessage chatMessage) {
            return ChatRoomMessageResponse.builder()
                    .messageId(chatMessage.getId())
                    .sender(UserDto.ResponseOnlyUserNameWithId.of(chatMessage.getSender()))
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
