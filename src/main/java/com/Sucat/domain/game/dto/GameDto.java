package com.Sucat.domain.game.dto;

import com.Sucat.domain.game.model.GameCategory;
import lombok.Builder;

import java.util.List;

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

    @Builder
    public record UserRankingWithDepartmentRankingResponse(
            int score,
            int ranking,
            int departmentRanking
    ) {
        public static UserRankingWithDepartmentRankingResponse of(UserRankingResponse userRankingResponse, int departmentRanking) {
            return UserRankingWithDepartmentRankingResponse.builder()
                    .score(userRankingResponse.getScore())
                    .ranking(userRankingResponse.getRanking())
                    .departmentRanking(departmentRanking)
                    .build();
        }
    }

    @Builder
    public record TopPlayersWithUserRankingResponse(
            List<GameRankingResponse> top10Players,
            UserRankingWithDepartmentRankingResponse rankingResponse
    ) {
        public static TopPlayersWithUserRankingResponse of(
                List<GameRankingResponse> top10Players,
                UserRankingWithDepartmentRankingResponse rankingResponse
        ) {
            return TopPlayersWithUserRankingResponse.builder()
                    .top10Players(top10Players)
                    .rankingResponse(rankingResponse)
                    .build();
        }
    }
}
