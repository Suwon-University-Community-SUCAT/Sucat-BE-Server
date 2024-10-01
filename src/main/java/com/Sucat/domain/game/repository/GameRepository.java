package com.Sucat.domain.game.repository;

import com.Sucat.domain.game.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
