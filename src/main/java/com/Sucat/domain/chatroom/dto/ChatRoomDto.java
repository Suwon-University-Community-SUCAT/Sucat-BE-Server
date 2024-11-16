package com.Sucat.domain.chatroom.dto;

import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.user.model.User;
import lombok.Builder;

import java.time.LocalDateTime;

public class ChatRoomDto {
    /**
     * Response
     */

    @Builder
    public record ChatRoomListResponse(
            String roomId,
            String receiverEmail,
            String receiverNickname,
            String profileImageUrl,
            LocalDateTime createTime
    ) {
        public static ChatRoomListResponse of(ChatRoom chatRoom, User receiver) {
            Image userImage = receiver.getUserImage();
            String imageUrl = (userImage != null) ? userImage.getImageUrl() : null;
            return ChatRoomListResponse.builder()
                    .roomId(chatRoom.getRoomId())
                    .receiverEmail(receiver.getEmail())
                    .receiverNickname(receiver.getNickname())
                    .profileImageUrl(imageUrl)
                    .createTime(chatRoom.getCreatedAt())
                    .build();
        }
    }

    @Builder
    public record ChatRoomCreationResponse(
            int status,
            String roomId
    ) {

    }
}
