package com.Sucat.domain.comment.domain;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.like.model.CommentLike;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.common.dao.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommentLike> likeList = new ArrayList<>();

    private String content;
    private int likeCount;
    private int commentCount;
    private boolean checkWriter; // 게시글 작성자가 작성한 댓글인지

    @Builder
    public Comment(Board board, User user, String content, boolean checkWriter) {
        this.board = board;
        this.user = user;
        this.content = content;
        this.likeCount = 0;
        this.commentCount = 0;
        this.checkWriter = checkWriter;
    }

    /* Using Method */
    public void addLike(CommentLike commentLike) {
        likeList.add(commentLike);
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount--;
    }
}
