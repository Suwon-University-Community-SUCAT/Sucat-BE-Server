package com.Sucat.domain.scrap.repository;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.scrap.model.Scrap;
import com.Sucat.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Scrap findByUserAndBoard(User user, Board board);
}
