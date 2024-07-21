package com.Sucat.domain.friendship.model;

import com.Sucat.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendShip {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "friend_ship_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String userEmail;

    private String friendEmail;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    private boolean isFrom; // 어디선가 보내온 요청? 보낸 요청일수도 있기 때문

    private Long counterpartId; // 상대 요청의 아이디

    /* Using Method */
    public void acceptRequest() {
        this.status = FriendshipStatus.ACCEPT;
    }

    public void setCounterpartId(Long id) {
        counterpartId = id;
    }

    @Builder
    public FriendShip(User user, String userEmail, String friendEmail, FriendshipStatus status, boolean isFrom) {
        this.user = user;
        this.userEmail = userEmail;
        this.friendEmail = friendEmail;
        this.status = status;
        this.isFrom = isFrom;
    }

    /* 연관관계 메서드 */

}
