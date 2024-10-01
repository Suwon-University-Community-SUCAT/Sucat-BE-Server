package com.Sucat.domain.game.repository;

import com.Sucat.domain.game.model.Game;
import com.Sucat.domain.game.model.GameCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByCategory(GameCategory category);
}
