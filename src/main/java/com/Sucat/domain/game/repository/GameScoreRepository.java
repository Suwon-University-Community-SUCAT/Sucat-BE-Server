package com.Sucat.domain.game.repository;

import com.Sucat.domain.game.model.GameCategory;
import com.Sucat.domain.game.model.GameScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static com.Sucat.domain.game.dto.GameDto.GameRankingResponse;
import static com.Sucat.domain.game.dto.GameDto.UserRankingResponse;

public interface GameScoreRepository extends JpaRepository<GameScore, Long> {
    GameScore findByUserIdAndGameId(Long userId, Long gameId);

    /* 상위 10명 점수 조회 */
    @Query(value = "SELECT u.id AS userId, u.nickname, gs.score " +
            "FROM GameScore gs " +
            "JOIN gs.user u " +
            "JOIN gs.game g " +
            "WHERE g.category = :category " +
            "ORDER BY gs.score DESC")
    List<GameRankingResponse> findTop10PlayersByCategory(@Param("category") GameCategory category, Pageable pageable);

    /* 사용자 개인 랭킹, 점수 조회 */
    @Query(value = "SELECT COALESCE(r.rank, 0) AS rank, COALESCE(r.score, 0) AS score " +
            "FROM (" +
            "    SELECT gs.user.id AS userId, gs.score, " +
            "           ROW_NUMBER() OVER (ORDER BY gs.score DESC) AS rank " +
            "    FROM GameScore gs " +
            "    JOIN gs.game g " +
            "    WHERE g.category = :category " +
            ") r " +
            "WHERE r.userId = :userId ")
    UserRankingResponse findUserRankingByCategory(@Param("userId") Long userId, @Param("category") GameCategory category);

}
