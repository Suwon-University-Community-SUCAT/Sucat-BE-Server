package com.Sucat.domain.board.model.repository;
import com.Sucat.domain.board.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 자유게시판, 사적게시판, 중고장터 별로 찾는 메소드 추가 가능
}

