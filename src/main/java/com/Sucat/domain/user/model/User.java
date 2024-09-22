package com.Sucat.domain.user.model;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.comment.domain.Comment;
import com.Sucat.domain.friendship.model.FriendShip;
import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.notify.model.Notify;
import com.Sucat.domain.scrap.model.Scrap;
import com.Sucat.global.common.dao.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "tblUser")// User는 예약어이기에 사용 불가
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @Email
    private String email;

    private String name;

    @NotNull
    private String password;

    private String intro;

    private String department;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    /*연관관계 메서드*/
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FriendShip> friendShipList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Notify> notifyList = new ArrayList<>(); // 회원의 알림 리스트

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Image userImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Board> boardList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scrap> scrapList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    /* 연관관계 메서드 */
    public void updateUserImage(Image image) {
        this.userImage = image;
    }

    public void deleteUserImage() {
        this.userImage = null;
    }

    public void addFriendShip(FriendShip friendShipTo) {
        this.friendShipList.add(friendShipTo);
    }

    public void addBoard(Board board) {
        this.boardList.add(board);
    }

    public void addScrap(Scrap scrap) {
        this.scrapList.add(scrap);
    }
    // 필요하다면 Scrap 리스트에서 제거하는 메서드
    public void removeScrap(Scrap scrap) {
        scrapList.remove(scrap);
    }

    public void addComment(Comment comment) {
        this.commentList.add(comment);
        comment.setUser(this);
    }

    public void removeComment(Comment comment) {
        commentList.remove(comment);
    }

    /* Using Method */
    // 비밀번호 변경 메서드
    public void resetPassword(String newPassword) {
        this.password = newPassword;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateRole() {
        this.role = UserRole.USER;
    }

    public void updateProfile(String nickname, String intro) {
        this.nickname = nickname;
        this.intro = intro;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
    public void updateIntro(String intro) {
        this.intro = intro;
    }
}
