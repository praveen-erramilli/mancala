package com.praveen.mancala.game;

import com.praveen.mancala.model.Game;
import com.praveen.mancala.model.GameStatus;
import com.praveen.mancala.model.Pit;

public class Picker {
    private final Long pitID;
    private final Game game;

    public Picker(Long pitID, Game game) {
        this.pitID = pitID;
        this.game = game;
    }

    public void runPicker() {
        if(game.getGameStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot run the game that is not in progress");
        }
        Pit pit = game.getBoard().fetchPit(pitID);

        if(!pit.canPickCoins(game)) {
            throw new UnsupportedOperationException("User is not allowed to pick from this pit");
        }
        int pickedCoins = pit.pickCoins(game);
        while (pickedCoins-- > 0) {
            if(pit.getNext().canInsert(game)) {
                pit = pit.getNext();
            } else {
                //if pit.next is the other player's big pit. So skip inserting into it
                pit = pit.getNext().getNext();
            }
            pit.insertCoin(game);
            if(pickedCoins == 0) {
                game.setLastInsertedPit(pit);
                pit.onLastCoinInsert(game);
            }
        }
    }
}
