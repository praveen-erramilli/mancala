package com.praveen.mancala.game;

import com.praveen.mancala.model.*;

import java.util.List;

import static com.praveen.mancala.AppConstants.NUM_PITS;

public class EmptyRowWatcher {
    private Game game;
    public EmptyRowWatcher(Game game) {
        this.game = game;
    }

    public void watchAndHandleEmptyRow() {
        Board board = game.getBoard();
        boolean foundEmpty0 = isEmptyRow(board.getPits0());
        boolean foundEmpty1 = isEmptyRow(board.getPits1());

        if(foundEmpty0) {
            game.setGameStatus(GameStatus.GAMEOVER);
            pushAllCoinsToMancala(game, board.getPits1(), board.getMancala1());
        } else if(foundEmpty1) {
            game.setGameStatus(GameStatus.GAMEOVER);
            pushAllCoinsToMancala(game, board.getPits0(), board.getMancala0());
        }

        if(foundEmpty0 || foundEmpty1) {
            Mancala mancala0 = board.getMancala0();
            Mancala mancala1 = board.getMancala1();

            if(mancala0.getCoinsCount() == mancala1.getCoinsCount()) {
                game.setTie(true);
            } else if(mancala0.getCoinsCount() < mancala1.getCoinsCount()) {
                game.setWinner(game.getPlayer1());
            } else {
                game.setWinner(game.getPlayer0());
            }
        }
    }

    private static boolean isEmptyRow(List<Pit> pits) {
        boolean foundEmpty = true;
        for(int i=0; i< NUM_PITS; i++) {
            if(pits.get(i).getCoinsCount() != 0) {
                foundEmpty = false;
                break;
            }
        }
        return foundEmpty;
    }

    private static void pushAllCoinsToMancala(Game game, List<Pit> pits, Mancala mancala) {
        for(int i=0; i< NUM_PITS; i++) {
            int pickedCoins = pits.get(i).pickCoins(game);
            mancala.insertMultipleCoins(game, pickedCoins);
        }
    }
}
