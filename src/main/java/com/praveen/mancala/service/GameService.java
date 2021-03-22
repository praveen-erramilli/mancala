package com.praveen.mancala.service;

import com.praveen.mancala.cache.IGameCache;
import com.praveen.mancala.exception.GameNotFoundException;
import com.praveen.mancala.game.EmptyRowWatcher;
import com.praveen.mancala.game.GameInitializer;
import com.praveen.mancala.model.Game;
import com.praveen.mancala.game.Mover;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final IGameCache gameCache;
    private final GameInitializer gameInitializer;

    @Autowired
    public GameService(IGameCache gameCache, GameInitializer gameInitializer) {
        this.gameCache = gameCache;
        this.gameInitializer = gameInitializer;
    }

    public Game getGame(Long id) {
        return gameCache.getGame(id).orElseThrow(() -> {
            throw new GameNotFoundException("Invalid argument passed for id. No such game found");
        });
    }

    public Game createGame() {
        //init a new game
        Game init = gameInitializer.init();
        return gameCache.saveGame(init);
    }

    public Game makeMove(Long id, Long pitID) {
        //ask picker to pick the pit and run
        Game game = getGame(id);

        Mover mover = new Mover(pitID, game);
        mover.makeMove();

        EmptyRowWatcher emptyRowWatcher = new EmptyRowWatcher(game);
        emptyRowWatcher.watchAndHandleEmptyRow();

        return gameCache.saveGame(game);
    }
}
