package com.Sucat.domain.friendship.dto;

import java.util.List;

public class FriendShipDto {

    /**
     * Request
     */


    /**
     * Response
     */
    public record WaitingFriendWithTotalCountResponse(
            List<WaitingFriendDto> waitingFriendDtoList,
            int totalCount
    ) {
        public static WaitingFriendWithTotalCountResponse of(List<WaitingFriendDto> waitingFriendDtoList, int totalCount) {
            return new WaitingFriendWithTotalCountResponse(waitingFriendDtoList, totalCount);
        }
    }
}
