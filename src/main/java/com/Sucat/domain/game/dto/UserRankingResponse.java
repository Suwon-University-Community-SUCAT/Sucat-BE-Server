package com.Sucat.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRankingResponse {
    private int ranking;
    private int score;
}
