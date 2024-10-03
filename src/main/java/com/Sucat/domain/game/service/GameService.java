package com.Sucat.domain.game.service;

import com.Sucat.domain.game.dto.GameRankingResponse;
import com.Sucat.domain.game.dto.UserRankingResponse;
import com.Sucat.domain.game.exception.GameException;
import com.Sucat.domain.game.model.DepartmentRanking;
import com.Sucat.domain.game.model.Game;
import com.Sucat.domain.game.model.GameCategory;
import com.Sucat.domain.game.model.GameScore;
import com.Sucat.domain.game.repository.DepartmentRankingRepository;
import com.Sucat.domain.game.repository.GameQueryRepository;
import com.Sucat.domain.game.repository.GameRepository;
import com.Sucat.domain.game.repository.GameScoreRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.Sucat.domain.game.dto.GameDto.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final GameQueryRepository gameQueryRepository;
    private final GameScoreRepository gameScoreRepository;
    private final DepartmentRankingRepository departmentRankingRepository;

    /* 회원 게임 점수 저장 */
    @Transactional
    public void saveScore(User user, GameScoreRequest gameScoreRequest) {
        GameCategory gameCategory = gameScoreRequest.gameCategory();
        int score = gameScoreRequest.score();

        Game game = findByGameCategory(gameCategory);
        Optional<GameScore> gameScoreOpt = gameScoreRepository.findByUserIdAndGameId(user.getId(), game.getId());
        GameScore gameScore;
        if (gameScoreOpt.isEmpty()) {
            // 기존 점수가 없으면 점수 초기화
            gameScore = GameScore.builder()
                    .user(user)
                    .game(game)
                    .score(score)
                    .build();
        } else {
            // 기존 점수가 있다면 점수 업데이트
            gameScore = gameScoreOpt.get();
            gameScore.updateScore(score);
        }

        List<DepartmentRanking> departmentRankings = game.getDepartmentRankings();
        Optional<DepartmentRanking> departmentRankingOpt = departmentRankings.stream().filter(d -> d.getDepartment() == user.getDepartment()).findFirst();
        DepartmentRanking departmentRanking = null;
        if (departmentRankingOpt.isEmpty()) {
            departmentRanking = new DepartmentRanking(user.getDepartment(), score, 0);
            game.addDepartmentRanking(departmentRanking);
        } else {
            departmentRanking = departmentRankingOpt.get();

            if (departmentRanking.getHighestScore() < score) {
                departmentRanking.updateHighestScore(score);
                game.updateRankings();
            }
        }

        departmentRankingRepository.save(departmentRanking);
        gameScoreRepository.save(gameScore);
        gameRepository.save(game);
    }

    /**
     * category와 일치하는 게임의 점수 top3
     * 사용자의 게임 점수 조회
     */
    public Top3PlayersWithUserScoreResponse getTop3PlayersWithUserScore(User user, GameCategory category) {
        List<GameRankingResponse> top3PlayersByCategory = gameQueryRepository.findTopPlayersByCategory(category, 3);

        Game game = findByGameCategory(category);
        Optional<GameScore> gameScoreOpt = gameScoreRepository.findByUserIdAndGameId(user.getId(), game.getId());
        int score = 0;

        if (gameScoreOpt.isPresent()) {
            score = gameScoreOpt.get().getScore();
        }

        return Top3PlayersWithUserScoreResponse.of(top3PlayersByCategory, score);
    }

    /*
    * category와 일치하는 게임의 점수 상위 10명 조회(내림차순 정렬)
    * 사용자의 게임 랭킹/점수 조회,사용자가 속한 학과의 랭킹 조회 - 반복 로직
    *
    * TODO: 랭킹 조회 방식을 고민중. DB에서 직접 조회하는 방식과 Game 엔티티 내에 랭킹 정보를 저장해두고 업데이트하는 방식 중 고민
    *  우선 초반에는 DB 직접 조회 방식을 채택하고, 성능에 문제가 있다면 수정하는 방향으로 결정
    * */
    public TopPlayersWithUserRankingResponse getTopPlayersWithUserRanking(User user, GameCategory category) {
        Game game = findByGameCategory(category);
        // top10 조회
        List<GameRankingResponse> top10PlayersByCategory = gameQueryRepository.findTopPlayersByCategory(category, 10);

        UserGameInfoResponse userGameInfoResponse = getUserRankingWithDepartmentRankingResponse(user, category);

        return TopPlayersWithUserRankingResponse.of(top10PlayersByCategory, userGameInfoResponse);
    }

    /*
    * category와 일치하는 게임의 전체 학과 랭킹 (DESC/내림차순)
    * 사용자의 게임 랭킹/점수 조회, 사용자가 속한 학과의 랭킹 조회 - 반복 로직
     */
    public AllDepartmentRankingWithUserRanking getAllDepartmentRankingWithUserRanking(User user, GameCategory category) {
        Game game = findByGameCategory(category);
        List<DepartmentRankingDto> departmentRankingDtoList = game.getDepartmentRankings().stream()
                .map(DepartmentRankingDto::of)
                .toList();
        UserGameInfoResponse userGameInfoResponse = getUserRankingWithDepartmentRankingResponse(user, category);

        return new AllDepartmentRankingWithUserRanking(departmentRankingDtoList, userGameInfoResponse);
    }

    /*Using Method*/
    public Game findByGameCategory(GameCategory category) {
        return gameRepository.findByCategory(category)
                .orElseThrow(() -> new GameException(ErrorCode.GAME_NOT_FOUND));
    }

    // 사용자의 게임 랭킹/점수 조회, 사용자 학과 랭킹 조회
    private UserGameInfoResponse getUserRankingWithDepartmentRankingResponse(User user, GameCategory category) {
        //사용자 랭킹/점수 조회
        UserRankingResponse userRankingInfo = getUserRank(user.getId(), category);

        // 사용자 학과 랭킹 조회
        Optional<DepartmentRanking> departmentRankingOpt = departmentRankingRepository.findByDepartment(user.getDepartment());
        UserGameInfoResponse userRankingResponse = null;
        if (departmentRankingOpt.isPresent()) {
            int rank = departmentRankingOpt.get().getRanking();
            userRankingResponse = UserGameInfoResponse.of(userRankingInfo, rank);
        } else {
            userRankingResponse = UserGameInfoResponse.of(userRankingInfo, 0); // 학과 랭킹이 존재하지 않는 경우 0으로 설정
        }
        return userRankingResponse;
    }

    // 사용자 랭킹 조회
    public UserRankingResponse getUserRank(Long userId, GameCategory gameCategory) {
        List<GameScore> scores = gameScoreRepository.findScoresByGameCategory(gameCategory);
        int ranking = 1;

        for (GameScore gameScore : scores) {
            if (gameScore.getUser().getId().equals(userId)) {
                return new UserRankingResponse(ranking, gameScore.getScore());
            }
            ranking++;
        }
        return new UserRankingResponse(0,0); // 사용자가 점수를 갖고 있지 않은 경우
    }

}
