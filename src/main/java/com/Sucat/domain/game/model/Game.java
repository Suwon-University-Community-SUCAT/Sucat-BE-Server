package com.Sucat.domain.game.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "game_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private GameCategory category;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameScore> scores; // 게임 점수 리스트

    public Game(GameCategory category) {
        this.category = category;
    }

    /* Using Method */
    // 회원별 랭킹 리스트 조회
    public List<GameScore> getUserRanking() {
        return scores.stream()
                .sorted((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore())) // 점수 내림차순 정렬
                .toList();
    }

    // 학과별 랭킹 리스트 조회
    public List<GameScore> getDepartmentRanking(String department) {
        return scores.stream()
                .filter(score -> score.getUser().getDepartment().name().equals(department)) // 해당 학과 필터링
                .sorted((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore())) // 점수 내림차순 정렬
                .toList();
    }
}
