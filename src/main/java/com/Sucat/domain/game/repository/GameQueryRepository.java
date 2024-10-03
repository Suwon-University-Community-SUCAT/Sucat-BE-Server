package com.Sucat.domain.game.repository;

import com.Sucat.domain.game.dto.GameRankingResponse;
import com.Sucat.domain.game.model.GameCategory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GameQueryRepository {
    private final EntityManager em;

    public List<GameRankingResponse> findTopPlayersByCategory(GameCategory category, int limit) {
        String query = "SELECT new com.Sucat.domain.game.dto.GameRankingResponse(u.id, u.nickname, u.department, gs.score) " +
                "FROM GameScore gs " +
                "JOIN gs.user u " +
                "JOIN gs.game g " +
                "WHERE g.category = :category " +
                "ORDER BY gs.score DESC";

        return em.createQuery(query, GameRankingResponse.class)
                .setParameter("category", category)
                .setMaxResults(limit)  // 상위 10명만 가져옴
                .getResultList();
    }
}
