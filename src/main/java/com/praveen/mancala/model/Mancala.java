package com.praveen.mancala.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class Mancala extends Pit {
    public Mancala(Long id, int coinsCount, Pit next, Player owner, Board board) {
        super(id, coinsCount, next, owner, null, board);
    }

    public Mancala() {
    }

    @Override
    public boolean canInsert(Game game) {
        //When game is over, pick all coins and put in mancala
        return game.getCurrentPlayer().equals(getOwner()) || game.getGameStatus().equals(GameStatus.GAMEOVER);
    }

    @Override
    public void onLastCoinInsert(Game game) {
        //current player gets another turn. Dont switch current player
        if(!canInsert(game)) {
            throw new UnsupportedOperationException("User is not allowed to add coin in this Pit");
        }
    }

    public void insertMultipleCoins(Game game, int count) {
        if(!canInsert(game)) {
            throw new UnsupportedOperationException("User is not allowed to add coin in this Pit");
        }
        super.setCoinsCount(super.getCoinsCount() + count);
    }

    @Override
    public boolean canPickCoins(Game game) {
        return false;
    }
}
