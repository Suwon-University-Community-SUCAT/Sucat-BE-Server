package com.Sucat.domain.game.controller;

import com.Sucat.domain.game.dto.GameDto;
import com.Sucat.domain.game.service.GameService;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.annotation.CurrentUser;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game")
public class GameController {
    private final GameService gameService;

    @PostMapping("/score")
    public ResponseEntity<ApiResponse<Object>> saveUserScore(
            @CurrentUser User user,
            GameDto.GameScoreRequest gameScoreRequest
    ) {
        gameService.saveScore(user, gameScoreRequest);

        return ApiResponse.onSuccess(SuccessCode._CREATED);
    }
}
