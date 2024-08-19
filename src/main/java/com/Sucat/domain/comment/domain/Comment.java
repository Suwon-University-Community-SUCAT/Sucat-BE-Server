package com.Sucat.domain.comment.domain;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.common.dao.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private String content;
    private int likeCount;
    private int commentCount;

    @Builder
    public Comment(Board board, User user, String content) {
        this.board = board;
        this.user = user;
        this.content = content;
        this.likeCount = 0;
        this.commentCount = 0;
    }
}
