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
            String friendInfo,
            String friendProfileImage
    ) {
        public static FriendSearchResponse of(FriendShip friendShip, User Friend) {
//            use.findByEmail(friendShip.getFriendEmail());
            return FriendSearchResponse.builder()
                    .id(friendShip.getId())
                    .friendEmail(friendShip.getFriendEmail())
                    .friendName(friendShip.getUser().getName())
                    .build();
//                    .friendDepartment(friendShip.ge)
        }
    }
}
