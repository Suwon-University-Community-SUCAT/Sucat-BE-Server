package com.Sucat.domain.chatroom.dto;

import com.Sucat.domain.chatmessage.dto.ChatRoomMessageResponseDto;
import com.Sucat.domain.chatroom.model.ChatRoom;
import com.Sucat.domain.user.dto.UserDto;
import com.Sucat.domain.user.model.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomDto {
    /**
     * Response
     */
    @Builder
    public record ChatRoomInfoWithMessagesResponse(
            Long roomId,
            UserDto.ResponseOnlyUserNameWithId currentUser,
            UserDto.ResponseOnlyUserNameWithId receiver,
            List<ChatRoomMessageResponseDto> chatRoomMessageResponseDtoList
    ) {
        public static ChatRoomInfoWithMessagesResponse of(Long roomId, User currentUser, User receiver, List<ChatRoomMessageResponseDto> chatRoomMessageResponseDtoList) {
            return ChatRoomInfoWithMessagesResponse.builder()
                    .roomId(roomId)
                    .currentUser(UserDto.ResponseOnlyUserNameWithId.of(currentUser))
                    .receiver(UserDto.ResponseOnlyUserNameWithId.of(receiver))
                    .chatRoomMessageResponseDtoList(chatRoomMessageResponseDtoList)
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

    @Builder
    public record ChatRoomCreationResponse(
            int status,
            String roomId
    ) {

    }
}
