package com.Sucat.domain.board.repository;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    /* 특정 카테고리의 게시글을 조회하는 메서드 */
    Page<Board> findByCategory(BoardCategory category, Pageable pageable);

    /* 카테고리 내에서 최근 3일간의 게시물 중 좋아요가 가장 많은 게시물을 조회하는 메서드 */
    Optional<Board> findTopByCategoryAndCreatedAtAfterOrderByLikeCountDesc(BoardCategory category, LocalDateTime dateTime);
}