package com.Sucat.domain.game.repository;

import com.Sucat.domain.game.dto.GameRankingResponse;
import com.Sucat.domain.game.model.GameCategory;
import com.Sucat.domain.game.model.GameScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameScoreRepository extends JpaRepository<GameScore, Long> {
    GameScore findByUserIdAndGameId(Long userId, Long gameId);

    /* 상위 10명 점수 조회 */
    @Query(value = "SELECT new com.Sucat.domain.game.dto.GameRankingResponse(u.id, u.nickname, u.department, gs.score) " +
            "FROM GameScore gs " +
            "JOIN gs.user u " +
            "JOIN gs.game g " +
            "WHERE g.category = :category " +
            "ORDER BY gs.score DESC")
    List<GameRankingResponse> findTop10PlayersByCategory(@Param("category") GameCategory category);

    /* 게임 카테고리와 일치하는 게임의 점수를 내림차순으로 정렬*/
    @Query("SELECT gs FROM GameScore gs WHERE gs.game.category = :gameCategory ORDER BY gs.score DESC")
    List<GameScore> findScoresByGameCategory(@Param("gameCategory") GameCategory gameCategory);



}
