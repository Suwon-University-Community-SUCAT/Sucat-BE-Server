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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game")
public class GameController {
    private final GameService gameService;

    /*게임 시작 전 유저 식별자 반환*/
    @GetMapping("/userId")
    public ResponseEntity<ApiResponse<Object>> getUserId(@CurrentUser User user) {
        return ApiResponse.onSuccess(SuccessCode._OK, user.getId());
    }

    /* 게임 점수 저장 */
    @MessageMapping("/score") // '/pub/api/v1/game/score'로 오는 웹소켓 요청 처리
    public ResponseEntity<ApiResponse<Object>> saveUserScore(
            @Payload GameDto.GameScoreRequest gameScoreRequest
    ) {
        gameService.saveScore(gameScoreRequest);

        return ApiResponse.onSuccess(SuccessCode._CREATED);
    }

    /* 게임 대기창 - 게임 점수 top3, 개인 점수 */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getTop3PlayersWithUserScore(@RequestParam(name="category") GameCategory category,
                                                                           @CurrentUser User user) {
        GameDto.Top3PlayersWithUserScoreResponse top3PlayersWithUserScore = gameService.getTop3PlayersWithUserScore(user, category);

        return ApiResponse.onSuccess(SuccessCode._OK, top3PlayersWithUserScore);
    }

    /* 게임 개인 랭킹 Top10 + 사용자 랭킹/점수/학과 랭킹 조회 */
    @GetMapping("/user/ranking")
    public ResponseEntity<ApiResponse<Object>> getTop10PlayersWithUserRanking(@RequestParam(name = "category") GameCategory category,
                                                                              @CurrentUser User user) {
        GameDto.TopPlayersWithUserRankingResponse topPlayersWithUserRanking = gameService.getTopPlayersWithUserRanking(user, category);

        return ApiResponse.onSuccess(SuccessCode._OK ,topPlayersWithUserRanking);
    }

    /* 게임 전체 학과 랭킹 + 사용자 랭킹/점수/학과 랭킹 조회 */
    @GetMapping("/department/ranking")
    public ResponseEntity<ApiResponse<Object>> getAllDepartmentRankingWithUserRanking(@RequestParam(name="category") GameCategory category,
                                                                                      @CurrentUser User user) {
        GameDto.AllDepartmentRankingWithUserRanking allDepartmentRankingWithUserRanking = gameService.getAllDepartmentRankingWithUserRanking(user, category);

        return ApiResponse.onSuccess(SuccessCode._OK, allDepartmentRankingWithUserRanking);
    }
}
