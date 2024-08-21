package com.Sucat.domain.like.model;

import com.Sucat.domain.comment.domain.Comment;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.common.dao.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLike extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_like_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id") // Board 삭제 -> Comment 자동 삭제 -> CommentLike 삭제, 사용자
    private Comment comment;

    /*연관관계 메서드*/
    @Builder
    public CommentLike(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }
}
