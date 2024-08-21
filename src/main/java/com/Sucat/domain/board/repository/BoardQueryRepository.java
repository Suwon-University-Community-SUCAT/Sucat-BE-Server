package com.Sucat.domain.board.repository;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardQueryRepository {
    private final EntityManager em;

    /* 카테고리 내에서 최근 3일간의 게시물 중 likeCount가 1 이상인 게시물 중에서 likeCount가 가장 높고, 같은 likeCount를 가진 게시물 중 최신 게시물 조회하는 쿼리 */
    public Optional<Board> findTopHotPost(BoardCategory category, LocalDateTime dateTime) {

        return em.createQuery(
                        "SELECT b " +
                                "FROM Board b " +
                                "WHERE b.category = :category AND b.createdAt > :dateTime AND b.likeCount >= 10 " +
                                "ORDER BY b.likeCount DESC, b.createdAt DESC", Board.class)
                .setParameter("category", category)
                .setParameter("dateTime", dateTime)
                .setMaxResults(1) // 결과를 1개로 제한
                .getResultList()
                .stream().findFirst();
    }

}
