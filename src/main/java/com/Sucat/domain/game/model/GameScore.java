package com.Sucat.domain.game.model;

import com.Sucat.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    private int score; // 사용자별 점수

    @Builder
    public GameScore(User user, Game game, int score) {
        this.user = user;
        this.game = game;
        this.score = score;
    }

    /* Using Method */
    // 점수 업데이트 메서드
    public void updateScore(int newScore) {
        if (newScore > this.score) {
            this.score = newScore; // 최고 점수 갱신
        }
    }
}
