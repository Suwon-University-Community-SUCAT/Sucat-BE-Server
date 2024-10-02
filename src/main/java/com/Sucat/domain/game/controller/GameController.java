package com.Sucat.domain.game.controller;

import com.Sucat.domain.game.dto.GameDto;
import com.Sucat.domain.game.model.GameCategory;
import com.Sucat.domain.game.service.GameService;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.annotation.CurrentUser;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game")
public class GameController {
    private final GameService gameService;

    /* 게임 점수 저장 */
    @PostMapping("/score")
    public ResponseEntity<ApiResponse<Object>> saveUserScore(
            @CurrentUser User user,
            @RequestBody GameDto.GameScoreRequest gameScoreRequest
    ) {
        gameService.saveScore(user, gameScoreRequest);

        return ApiResponse.onSuccess(SuccessCode._CREATED);
    }

    /* 게임 개인 랭킹 Top10 + 사용자 랭킹/점수/학과 랭킹 조회 */
    @GetMapping("/user/ranking")
    public ResponseEntity<ApiResponse<Object>> getTop10PlayersWithUserRanking(@RequestParam(name = "category") GameCategory category,
                                                                              @CurrentUser User user) {
        GameDto.TopPlayersWithUserRankingResponse topPlayersWithUserRanking = gameService.getTopPlayersWithUserRanking(user, category);

        return ApiResponse.onSuccess(SuccessCode._OK ,topPlayersWithUserRanking);
    }
}
