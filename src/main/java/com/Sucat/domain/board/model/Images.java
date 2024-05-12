package com.Sucat.domain.board.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static jakarta.persistence.GenerationType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Images {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @NotNull
//    @URL /* 올바른 url 형식이 아니라면 검증 부분에서 에러를 발생시킴 */
    private String imageUrl;

    public static Images of(Board board, String imageUrl) {

        return Images.builder()
                .board(board) //연관관계 설정
                .imageUrl(imageUrl)
                .build();

    }
}
