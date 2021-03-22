package com.praveen.mancala.cache;

import com.praveen.mancala.model.Game;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IGameCache {
    Optional<Game> getGame(Long gameId);

    Game saveGame(Game game);
}
