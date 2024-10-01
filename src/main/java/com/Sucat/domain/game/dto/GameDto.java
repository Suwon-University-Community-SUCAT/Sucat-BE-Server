package com.Sucat.domain.game.dto;

import com.Sucat.domain.game.model.GameCategory;

public class GameDto {
    /**
     * Request
     */
    public record GameScoreRequest(
            GameCategory gameCategory,
            int score
    ) {

    }


    /**
     * Response
     */
}
