package com.Sucat.domain.board.model;

//public class BoardRepository {
//    public Board save(Board board) {
//    }
//}
import com.Sucat.domain.board.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
}