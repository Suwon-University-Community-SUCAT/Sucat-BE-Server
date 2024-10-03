package com.Sucat.domain.game.dto;

import com.Sucat.domain.game.model.DepartmentRanking;
import com.Sucat.domain.game.model.GameCategory;
import com.Sucat.domain.user.model.Department;
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
    public record UserGameInfoResponse(
            int score,
            int ranking,
            int departmentRanking
    ) {
        public static UserGameInfoResponse of(UserRankingResponse userRankingResponse, int departmentRanking) {
            return UserGameInfoResponse.builder()
                    .score(userRankingResponse.getScore())
                    .ranking(userRankingResponse.getRanking())
                    .departmentRanking(departmentRanking)
                    .build();
        }
    }

    @Builder
    public record TopPlayersWithUserRankingResponse(
            List<GameRankingResponse> top10Players,
            UserGameInfoResponse userGameInfo
    ) {
        public static TopPlayersWithUserRankingResponse of(
                List<GameRankingResponse> top10Players,
                UserGameInfoResponse rankingResponse
        ) {
            return TopPlayersWithUserRankingResponse.builder()
                    .top10Players(top10Players)
                    .userGameInfo(rankingResponse)
                    .build();
        }
    }

    @Builder
    public record Top3PlayersWithUserScoreResponse(
            List<GameRankingResponse> top3Players,
            int score
    ) {
        public static Top3PlayersWithUserScoreResponse of(
                List<GameRankingResponse> top3Players,
                int score
        ) {
            return Top3PlayersWithUserScoreResponse.builder()
                    .top3Players(top3Players)
                    .score(score)
                    .build();
        }
    }

    @Builder
    public record DepartmentRankingDto(
            Department department,
            int highScore,
            int ranking
    ) {
        public static DepartmentRankingDto of(DepartmentRanking departmentRanking) {
            return DepartmentRankingDto.builder()
                    .department(departmentRanking.getDepartment())
                    .highScore(departmentRanking.getHighestScore())
                    .ranking(departmentRanking.getRanking())
                    .build();
        }
    }

    public record AllDepartmentRankingWithUserRanking(
            List<DepartmentRankingDto> departmentRankingDtoList,
            UserGameInfoResponse userGameInfo
    ) {
    }
}
