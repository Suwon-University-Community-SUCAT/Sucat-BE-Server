package com.Sucat.domain.game.service;

import com.Sucat.domain.game.repository.GameRepository;
import com.Sucat.domain.game.repository.GameScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final GameScoreRepository gameScoreRepository;
}
