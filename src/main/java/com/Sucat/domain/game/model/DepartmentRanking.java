package com.Sucat.domain.game.model;

import com.Sucat.domain.user.model.Department;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepartmentRanking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_ranking_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Department department;

    private int highestScore = 0;
    private int ranking = 0;

    @ManyToOne(fetch = FetchType.LAZY) // Game과의 관계를 설정
    @JoinColumn(name = "game_id") // 외래 키 컬럼 설정
    private Game game;

    public DepartmentRanking(Department department, int highestScore, int ranking) {
        this.department = department;
        this.highestScore = highestScore;
        this.ranking = ranking;
    }

    /* Using Method */
    public void updateHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public void updateRank(int ranking) {
        this.ranking = ranking;
    }

    public void addGame(Game game) {
        this.game = game;
    }
}
