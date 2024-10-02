package com.Sucat.domain.game.model;

import com.Sucat.domain.user.model.Department;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "department")
    private List<DepartmentRanking> departmentRankings;

    public Game(GameCategory category) {
        this.category = category;
    }

    /* Using Method */

    /* 학과 랭킹 업데이트 - 학과 별 최고 점수가 갱신되는 경우에만 실행*/
    public void updateDepartmentRanking(Department department, int highestScore) {
        // 랭킹 업데이트
        DepartmentRanking ranking = departmentRankings.stream()
                .filter(r -> r.getDepartment() == department)
                .findFirst()
                .orElse(new DepartmentRanking(department, highestScore, 0));

        ranking.updateHighestScore(highestScore);
        updateRankings(); // 모든 랭킹을 업데이트하는 메서드 호출
    }

    // 학과 랭킹 업데이트
    private void updateRankings() {
        // 모든 학과의 랭킹을 내림차순으로 정렬
        List<DepartmentRanking> sortedRankings = departmentRankings.stream()
                .sorted(Comparator.comparingInt(DepartmentRanking::getHighestScore).reversed())
                .toList();

        // 순위를 부여
        for (int i = 0; i < sortedRankings.size(); i++) {
            sortedRankings.get(i).updateRank(i + 1);
        }
    }
}
