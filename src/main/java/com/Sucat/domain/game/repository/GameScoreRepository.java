package com.Sucat.domain.game.repository;

import com.Sucat.domain.game.model.GameScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameScoreRepository extends JpaRepository<GameScore, Long> {
}
