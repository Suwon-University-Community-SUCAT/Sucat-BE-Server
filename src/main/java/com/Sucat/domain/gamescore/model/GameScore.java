package com.Sucat.domain.gamescore.model;

import com.Sucat.global.common.dao.BaseEntity;
import com.Sucat.domain.game.model.Game;
import com.Sucat.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameScore extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "game_score_id")
    private Long id;

    private int highScore;

    private int totalScore;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "game_id")
    private Game game;
}
