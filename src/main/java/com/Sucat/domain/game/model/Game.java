package com.Sucat.domain.game.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
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

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DepartmentRanking> departmentRankings = new ArrayList<>();

    public Game(GameCategory category) {
        this.category = category;
    }

    /* Using Method */
    public void addDepartmentRanking(DepartmentRanking departmentRanking) {
        this.departmentRankings.add(departmentRanking);
        departmentRanking.addGame(this);
        updateRankings();
    }

    // 학과 랭킹 업데이트
    public void updateRankings() {
        // 모든 학과의 랭킹을 내림차순으로 정렬
        List<DepartmentRanking> sortedRankings = departmentRankings.stream()
                .sorted(Comparator.comparingInt(DepartmentRanking::getHighestScore).reversed())
                .toList();

        // 순위를 부여
        int i = 1;
        for (DepartmentRanking departmentRanking : sortedRankings) {
            departmentRanking.updateRank(i);
            i++;
        }
    }
}
