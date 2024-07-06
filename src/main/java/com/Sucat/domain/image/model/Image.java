package com.Sucat.domain.image.model;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @NotNull
//    @URL
    private String imageUrl;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public static Image ofUser(User user, String imageUrl) {

        return Image.builder()
                .user(user) //연관관계 설정
                .imageUrl(imageUrl)
                .build();

    }

    public static Image ofBoard(Board board, String imageUrl) {

        return Image.builder()
                .board(board) //연관관계 설정
                .imageUrl(imageUrl)
                .build();

    }
}
