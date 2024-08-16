package com.Sucat.domain.board.comment;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
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

    private String content;
    private LocalDateTime minute;
    private int likeCount;
    private int commentCount;
    private int scrapCount;

    public Comment(Board board, User user, String content) {
        this.board = board;
        this.user = user;
        this.content = content;
        this.minute = LocalDateTime.now();
        this.likeCount = 0;
        this.commentCount = 0;
        this.scrapCount = 0;
    }
}
