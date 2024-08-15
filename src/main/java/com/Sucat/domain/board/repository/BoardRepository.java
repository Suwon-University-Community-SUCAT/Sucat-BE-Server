package com.Sucat.domain.board.repository;

//public class BoardRepository {
//    public Board save(Board board) {
//    }
//}
import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByCategory(BoardCategory category);
}