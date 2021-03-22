package com.praveen.mancala.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.praveen.mancala.model.Game;
import com.praveen.mancala.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class GameCache implements IGameCache {
    // assumes there is only one app server

    private final GameRepository gameRepository;

    @Autowired
    public GameCache(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    private static final Cache<Long, Optional<Game>> CACHE = Caffeine.newBuilder()
            .maximumSize(100)
            .build();

    @Override
    public Optional<Game> getGame(Long gameId) {
        return CACHE.get(gameId, ((key) -> gameRepository.findById(gameId)));
    }

    @Override
    public Game saveGame(Game game) {
        Game savedGame = gameRepository.save(game);
        CACHE.put(game.getId(), Optional.of(savedGame));
        return savedGame;
    }
}
