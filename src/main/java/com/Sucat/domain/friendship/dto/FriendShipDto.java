package com.Sucat.domain.friendship.dto;

import com.Sucat.domain.friendship.model.FriendShip;
import com.Sucat.domain.user.model.User;
import lombok.Builder;

public class FriendShipDto {

    /**
     * Request
     */


    /**
     * Response
     */
    @Builder
    public record FriendSearchResponse(
            Long id,
            String friendEmail,
            String friendName,
            String friendDepartment,
            String friendIntro,
            String friendProfileImageName
    ) {
        public static FriendSearchResponse of(FriendShip friendShip, User friend) {
            return FriendSearchResponse.builder()
                    .id(friendShip.getId())
                    .friendEmail(friendShip.getFriendEmail())
                    .friendName(friendShip.getUser().getName())
                    .friendDepartment(friend.getDepartment())
                    .friendIntro(friend.getIntro())
                    .friendProfileImageName(friend.getUserImage().getImageName())
                    .build();
        }
    }
}
