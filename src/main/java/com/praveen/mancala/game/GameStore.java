package com.praveen.mancala.game;

import lombok.Data;

@Data
public class GameStore {

    private Long gameId;

    public void clear() {
        this.gameId = null;
    }
}
