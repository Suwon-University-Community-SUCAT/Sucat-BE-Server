package com.Sucat.domain.board.model;

import com.Sucat.domain.comment.domain.Comment;
import com.Sucat.domain.user.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Board {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @NotNull
    private String name;

    private String title;

    private String content;

    private int likeCount;

    private int commentCount;

    private int scrapCount;

    private LocalDateTime minute;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BoardCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Board(String name, String title, String content, BoardCategory category) {
        this.name = name;
        this.title = title;
        this.content = content;
        this.likeCount = 0; //초기 좋아요 수
        this.commentCount = 0;  //초기 댓글 수
        this.scrapCount = 0;    //초기 스크랩 수
        this.minute = LocalDateTime.now();  //현재 시간을 게시 시간으로 설정
        this.category = category;
        //this.images = new ArrayList<>();
    }

    public void addUser(User user) {
        this.user = user;
    }

    public void updateBoard(String title, String content/*, BoardCategory category*/) {
        this.title = title;
        this.content = content;
        /*this.category = category;*/
    }
}
