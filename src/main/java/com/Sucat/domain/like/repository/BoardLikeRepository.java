package com.Sucat.domain.like.repository;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.like.model.BoardLike;
import com.Sucat.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    BoardLike findByUserAndBoard(User user, Board board);
}
