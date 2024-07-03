package com.Sucat.domain.friendship.model;

import com.Sucat.global.common.dao.BaseEntity;
import com.Sucat.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendShip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "friend_ship_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String userEmail;

    private String friendEmail;

    private FriendshipStatus status;

    private boolean isFrom;

    private Long counterpartId;

    public void acceptFriendshipRequest() {
        status = FriendshipStatus.ACCEPT;
    }

    public void setCounterpartId(Long id) {
        counterpartId = id;
    }
}
