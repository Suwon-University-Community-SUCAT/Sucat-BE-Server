package com.Sucat.domain.chatroom.dto;

import com.Sucat.domain.user.dto.UserDto;
import com.Sucat.domain.user.model.User;
import lombok.Builder;

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
}
