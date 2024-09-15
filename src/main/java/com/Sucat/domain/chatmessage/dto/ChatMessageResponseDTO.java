package com.Sucat.domain.chatmessage.dto;

import com.Sucat.domain.user.dto.UserDto;
import com.Sucat.domain.user.model.User;
import lombok.Builder;

import java.util.List;

public class ChatMessageResponseDTO {
    /**
     * Response
     */
    @Builder
    public record ChatRoomOpenWithMessagesResponse(
            Long roomId,
            UserDto.ResponseOnlyUserNameWithId currentUser,
            UserDto.ResponseOnlyUserNameWithId receiver,
            List<ChatRoomMessageResponseDto> chatRoomMessageResponseDtoList
    ) {
        public static ChatRoomOpenWithMessagesResponse of(Long roomId, User currentUser, User receiver, List<ChatRoomMessageResponseDto> chatRoomMessageResponseDtoList) {
            return ChatRoomOpenWithMessagesResponse.builder()
                    .roomId(roomId)
                    .currentUser(UserDto.ResponseOnlyUserNameWithId.of(currentUser))
                    .receiver(UserDto.ResponseOnlyUserNameWithId.of(receiver))
                    .chatRoomMessageResponseDtoList(chatRoomMessageResponseDtoList)
                    .build();
        }
    }

    @Builder
    public record InfiniteScrollMessagesResponse(
            Long roomId,
            List<ChatRoomMessageResponseDto> chatRoomMessageResponseDtoList
    ) {
        public static InfiniteScrollMessagesResponse of(Long roomId, List<ChatRoomMessageResponseDto> chatRoomMessageResponseDtoList) {
            return InfiniteScrollMessagesResponse.builder()
                    .roomId(roomId)
                    .chatRoomMessageResponseDtoList(chatRoomMessageResponseDtoList)
                    .build();
        }
    }
}
