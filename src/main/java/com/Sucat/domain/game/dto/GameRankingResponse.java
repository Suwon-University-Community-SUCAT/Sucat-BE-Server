package com.Sucat.domain.game.dto;

import com.Sucat.domain.user.model.Department;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameRankingResponse {
    private Long userId;
    private String userNickname;
    private Department department;
    private int score;
}
