package com.Sucat.domain.chatroom.dto;

import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.user.dto.UserDto;
import com.Sucat.domain.user.model.User;
import lombok.Builder;

import java.time.LocalDateTime;

public class ChatRoomDto {
    /**
     * Response
     */
    @Builder
    public record RoomResponse(
            Long roomId,
            UserDto.ResponseOnlyUserNameWithId sender,
            UserDto.ResponseOnlyUserNameWithId receiver
    ) {
        public static RoomResponse of(Long roomId, User sender, User receiver) {
            return RoomResponse.builder()
                    .roomId(roomId)
                    .sender(UserDto.ResponseOnlyUserNameWithId.of(sender))
                    .receiver(UserDto.ResponseOnlyUserNameWithId.of(receiver))
                    .build();
        }
    }

    @Builder
    public record ChatRoomListResponse(
            String roomId,
            String receiverEmail,
            String receiverNickname,
            String profileImageName,
            LocalDateTime createTime
    ) {
        public static ChatRoomListResponse of(ChatRoom chatRoom, User receiver) {
            return ChatRoomListResponse.builder()
                    .roomId(chatRoom.getRoomId())
                    .receiverEmail(receiver.getEmail())
                    .receiverNickname(receiver.getNickname())
                    .profileImageName(receiver.getUserImage().getImageName())
                    .createTime(chatRoom.getCreatedAt())
                    .build();
        }
    }
}
