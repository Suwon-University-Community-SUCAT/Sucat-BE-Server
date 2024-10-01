package com.Sucat.domain.game.service;

import com.Sucat.domain.game.dto.GameDto;
import com.Sucat.domain.game.exception.GameException;
import com.Sucat.domain.game.model.Game;
import com.Sucat.domain.game.model.GameCategory;
import com.Sucat.domain.game.model.GameScore;
import com.Sucat.domain.game.repository.GameRepository;
import com.Sucat.domain.game.repository.GameScoreRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final GameScoreRepository gameScoreRepository;

    /* 회원 게임 점수 저장 */
    @Transactional
    public void saveScore(User user, GameDto.GameScoreRequest gameScoreRequest) {
        GameCategory gameCategory = gameScoreRequest.gameCategory();
        int score = gameScoreRequest.score();

        Game game = findByGameCategory(gameCategory);
        GameScore gameScore = gameScoreRepository.findByUserIdAndGameId(user.getId(), game.getId());

        if (gameScore == null) {
            // 기존 점수가 없으면 점수 초기화
            gameScore = GameScore.builder()
                    .user(user)
                    .game(game)
                    .score(score)
                    .build();
        } else {
            // 기존 점수가 있다면 점수 업데이트
            gameScore.updateScore(score);
        }

        gameScoreRepository.save(gameScore);
    }


    /*Using Method*/
    public Game findByGameCategory(GameCategory category) {
        return gameRepository.findByCategory(category)
                .orElseThrow(() -> new GameException(ErrorCode.GAME_NOT_FOUND));
    }
}
