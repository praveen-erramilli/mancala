package com.praveen.mancala.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Data
public class BigPit extends Pit {
    public BigPit(Long id, int coinsCount, Pit next, Player owner, int ownerNumber, Board board) {
        super(id, coinsCount, next, owner, ownerNumber, null, board);
    }

    public BigPit() {
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

    @Override
    public String toString() {
        return super.toString();
    }
}
